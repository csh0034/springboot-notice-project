package com.ask.project.api.attachment.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ask.core.exception.InvalidationException;
import com.ask.core.security.SecurityUser;
import com.ask.core.security.SecurityUtils;
import com.ask.core.util.CoreUtils;
import com.ask.core.util.CoreUtils.date;
import com.ask.core.util.CoreUtils.file;
import com.ask.core.util.CoreUtils.string;
import com.ask.core.util.CoreUtils.webutils;
import com.ask.project.api.attachment.domain.ComtAttachment;
import com.ask.project.api.attachment.domain.ComtAttachmentGroup;
import com.ask.project.api.attachment.repository.AttachmentGroupRepository;
import com.ask.project.api.attachment.repository.AttachmentRepository;

@Service
@Transactional
public class AttachmentService {
	public final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final String SINGLE_FILE_GROUP_ID = "SINGLE-FILE-GROUP-ID";
	public static final int MAX_FILE_NM_LEN = 500;

	@Autowired
	private SecurityUtils securityUtils;

    @Autowired
    private AttachmentRepository attRepository;

    @Autowired
    private AttachmentGroupRepository attGrpRepository;

    public List<ComtAttachment> getFileInfors_group(String attachmentGroupId) {
		return attRepository.findByAttachmentGroupId(attachmentGroupId);
	}

    public ComtAttachment getFileInfor(String attachmentId) {
    	ComtAttachment attachment = attRepository.findById(attachmentId).orElse(null);
		if (attachment == null) {
			return null;
		}
		return attachment;
	}

    public void downloadFile(HttpServletResponse response, String uploadRootDir, String attachmentId) {
    	ComtAttachment attachment = attRepository.findById(attachmentId).orElseThrow(() -> new InvalidationException("파일이 없습니다."));

    	attachment.setDownloadCnt(attachment.getDownloadCnt() + 1);

    	attRepository.save(attachment);

		File file = new File(uploadRootDir + attachment.getSavedFilePath());
		webutils.downloadFile(response, file, attachment.getFileNm());
	}

    public boolean removeFile(String uploadRootDir, String attachmentId) {
    	ComtAttachment attachment = attRepository.findById(attachmentId).orElseThrow(() -> new InvalidationException("파일이 없습니다."));

    	attRepository.deleteById(attachmentId);

		boolean exists = true;
		if (!string.equals(SINGLE_FILE_GROUP_ID, attachment.getAttachmentGroupId())) {

			exists = attRepository.existsByAttachmentGroupId(attachment.getAttachmentGroupId());

			if (!exists) {
				attGrpRepository.deleteById(attachment.getAttachmentGroupId());
			}
		}

		File file = new File(uploadRootDir + attachment.getSavedFilePath());
		file.delete();

		return exists;
	}

    public void removeFiles_group(String uploadRootDir, String attachmentGroupId) {
		if (string.equals(SINGLE_FILE_GROUP_ID, attachmentGroupId)) {
			throw new InvalidationException("올바른 사용법이 아닙니다.");
		}
		ComtAttachmentGroup group = attGrpRepository.findById(attachmentGroupId).orElse(null);
		if (group == null) {
			return;
		}

		List<ComtAttachment> files = attRepository.findByAttachmentGroupId(attachmentGroupId);
		for (ComtAttachment file : files) {
			attRepository.deleteById(file.getAttachmentId());
			File f = new File(uploadRootDir + file.getSavedFilePath());
			f.delete();
		}
		attGrpRepository.deleteById(attachmentGroupId);
	}

	public ComtAttachmentGroup uploadFiles_toNewGroup(String uploadRootDir, MultipartFile[] multipartFiles, String[] allowedFileExts, long maxFileSize) {
        boolean fileExists = multipartFiles != null && multipartFiles.length > 0;
        if (!fileExists) {
            throw new InvalidationException("저장할 파일이 없습니다.");
        }
        if (string.isEmpty(uploadRootDir)) {
            throw new InvalidationException("파일을 저장할 디렉토리 정보가 입력되지 않았습니다.");
        }

        Date now = new Date();
        SecurityUser user = securityUtils.getUser();
        if (user == null) {
        	user = securityUtils.getAnonymousUser();
        }

        String dir = date.format(now, "/yyyy/MM");
        String fullDir = uploadRootDir + dir;
        try {
            File fdir = new File(fullDir);
            if (!fdir.exists()) {
                file.forceMkdir(new File(fullDir));
            }
        } catch (IOException e) {
            this.logger.error("Fail to create directory '" + fullDir + "'");
            throw new InvalidationException("파일을 저장할 디렉토리를 만들 수 없습니다.");
        }

        // 파일 검사
        long fileSize = 0;
        for (MultipartFile file: multipartFiles) {
            String filename = file.getOriginalFilename();
            if (filename.length() > MAX_FILE_NM_LEN) {
                throw new InvalidationException("파일명은 " + MAX_FILE_NM_LEN + "자를 넘을 수 없습니다.");
            }
            if (allowedFileExts != null && allowedFileExts.length > 0) {
                String ext = CoreUtils.filename.getExtension(filename);
                if (string.isEmpty(ext) || !string.containsIgnoreCase(allowedFileExts, ext)) {
                    throw new InvalidationException("지원하지 않는 파일 확장자가 포함되어 있습니다.(" + ext + ")");
                }
            }
            fileSize += file.getSize();
        }

        if (maxFileSize > 0 && fileSize > maxFileSize) {
        	throw new InvalidationException("저장가능한 파일크기(" + string.formatFileSize(maxFileSize) + ")를 초과하였습니다.");
        }

        // 첨부파일 그룹 생성
        ComtAttachmentGroup newAttachmentGroup = new ComtAttachmentGroup();
        newAttachmentGroup.setCreatedDt(now);
        newAttachmentGroup.setCreatorId(user.getUserId());

        ComtAttachmentGroup attachmentGroup = attGrpRepository.save(newAttachmentGroup);

        List<ComtAttachment> attachments = new ArrayList<>();
        ComtAttachment attachment = null;

        for (MultipartFile file: multipartFiles) {
            String filename = file.getOriginalFilename();
            String attachmentId = string.getNewId("file-");
            String savedFilePath = dir + "/" + attachmentId;
            try {
                file.transferTo(new File(uploadRootDir + savedFilePath));
            } catch (IllegalStateException | IOException e) {
                this.logger.error("Fail to save file: " + uploadRootDir + savedFilePath);
                throw new InvalidationException("시스템 문제로 파일을 저장할 수 없습니다.");
            }

            attachment = new ComtAttachment();
            attachment.setAttachmentGroupId(attachmentGroup.getAttachmentGroupId());
            attachment.setContentType(file.getContentType());
            attachment.setCreatedDt(now);
            attachment.setCreatorId(user.getUserId());
            attachment.setSavedFilePath(savedFilePath);
            attachment.setDownloadCnt(0L);
            attachment.setFileDeleted(false);
            attachment.setFileNm(filename);
            attachment.setFileSize(file.getSize());

            attachments.add(attachment);
        }

        attRepository.saveAll(attachments);

        return attachmentGroup;
	}

	public List<ComtAttachment> uploadFiles_toGroup(String uploadRootDir, String attachmentGroupId, MultipartFile[] multipartFiles, String[] allowedFileExts, long maxFileSize) {
        boolean fileExists = multipartFiles != null && multipartFiles.length > 0;
        if (!fileExists) {
            throw new InvalidationException("저장할 파일이 없습니다.");
        }
        if (string.isEmpty(uploadRootDir)) {
            throw new InvalidationException("파일을 저장할 디렉토리 정보가 입력되지 않았습니다.");
        }

        Date now = new Date();
        SecurityUser user = securityUtils.getUser();
        if (user == null) {
        	user = securityUtils.getAnonymousUser();
        }

        String dir = date.format(now, "/yyyy/MM");
        String fullDir = uploadRootDir + dir;
        try {
            File fdir = new File(fullDir);
            if (!fdir.exists()) {
                file.forceMkdir(new File(fullDir));
            }
        } catch (IOException e) {
            this.logger.error("Fail to create directory '" + fullDir + "'");
            throw new InvalidationException("파일을 저장할 디렉토리를 만들 수 없습니다.");
        }

        // 파일 검사
        long fileSize = 0;
        List<ComtAttachment> atts = attRepository.findByAttachmentGroupId(attachmentGroupId);
        for (ComtAttachment att : atts) {
        	fileSize += att.getFileSize();
        }

        for (MultipartFile file: multipartFiles) {
            String filename = file.getOriginalFilename();
            if (filename.length() > MAX_FILE_NM_LEN) {
                throw new InvalidationException("파일명은 " + MAX_FILE_NM_LEN + "자를 넘을 수 없습니다.");
            }
            String ext = CoreUtils.filename.getExtension(filename);
            if (allowedFileExts != null && allowedFileExts.length > 0) {
                if (string.isEmpty(ext) || !string.containsIgnoreCase(allowedFileExts, ext)) {
                    throw new InvalidationException("지원하지 않는 파일 확장자가 포함되어 있습니다.(" + ext + ")");
                }
            }
            fileSize += file.getSize();
        }

        if (maxFileSize > 0 && fileSize > maxFileSize) {
        	throw new InvalidationException("저장가능한 파일크기(" + string.formatFileSize(maxFileSize) + ")를 초과하였습니다.");
        }

        List<ComtAttachment> attachments = new ArrayList<>();
        ComtAttachment attachment = null;

        for (MultipartFile file: multipartFiles) {
            String filename = file.getOriginalFilename();
            String attachmentId = string.getNewId("file-");
            String savedFilePath = dir + "/" + attachmentId;
            try {
                file.transferTo(new File(uploadRootDir + savedFilePath));
            } catch (IllegalStateException | IOException e) {
                this.logger.error("Fail to save file: " + uploadRootDir + savedFilePath);
                throw new InvalidationException("시스템 문제로 파일을 저장할 수 없습니다.");
            }

            attachment = new ComtAttachment();
            attachment.setAttachmentGroupId(attachmentGroupId);
            attachment.setContentType(file.getContentType());
            attachment.setCreatedDt(now);
            attachment.setCreatorId(user.getUserId());
            attachment.setSavedFilePath(savedFilePath);
            attachment.setDownloadCnt(0L);
            attachment.setFileDeleted(false);
            attachment.setFileNm(filename);
            attachment.setFileSize(file.getSize());

            attachments.add(attachment);
        }

        attRepository.saveAll(attachments);

        return attachments;
	}
}
