package com.ask.project.api.notice.web;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ask.core.config.BasicProperties;
import com.ask.core.exception.InvalidationException;
import com.ask.core.security.SecurityUser;
import com.ask.core.security.SecurityUtils;
import com.ask.core.util.CoreUtils.string;
import com.ask.project.api.attachment.service.AttachmentService;
import com.ask.project.api.attachment.vo.AttachmentVO;
import com.ask.project.api.notice.service.NoticeService;
import com.ask.project.api.notice.vo.NoticeVO;
import com.ask.project.common.util.pagination.CorePagination;
import com.ask.project.common.util.pagination.CorePaginationParam;

@RestController
public class NoticeController {

	@Autowired
	private NoticeService noticeService;

	@Autowired
	private AttachmentService attService;

	@Autowired
	private SecurityUtils securityUtils;

	@Autowired
	private BasicProperties properties;

	@GetMapping("/api/notice")
	public CorePagination<NoticeVO> list(CorePaginationParam param, String title, Date beginDt, Date endDt) {
		return noticeService.selectNoticeList(param, title, beginDt, endDt);
	}

	@GetMapping("/api/notice/{noticeId}/files")
	public List<AttachmentVO> fileList(@PathVariable String noticeId) {
		NoticeVO notice = noticeService.selectNoticeInfo(noticeId);
		return attService.getFileInfors_group(notice.getAttachmentGroupId());
	}

	@GetMapping("/api/notice/{noticeId}/files/{fileId}")
	public void fileDownload(HttpServletResponse response, @PathVariable String noticeId, @PathVariable String fileId) {
		NoticeVO notice = noticeService.selectNoticeInfo(noticeId);

		AttachmentVO attachment = attService.getFileInfor(fileId);

		if (!string.equals(attachment.getAttachmentGroupId(), notice.getAttachmentGroupId())) {
			throw new InvalidationException("등록된 파일이 없습니다.");
		}

		attService.downloadFile(response, properties.getUploadDir(), fileId);
	}

	@DeleteMapping("/svc/api/notice/{noticeId}/files/{fileId}")
	public void fileDelete(@PathVariable String noticeId, @PathVariable String fileId) {
		NoticeVO notice = noticeService.selectNoticeInfo(noticeId);

		SecurityUser user = securityUtils.getUser();

		if (!string.equals(user.getUserId(), notice.getCreatorId())) {
			throw new InvalidationException("권한이 없습니다.");
		}

		AttachmentVO attachment = attService.getFileInfor(fileId);

		if (!string.equals(attachment.getAttachmentGroupId(), notice.getAttachmentGroupId())) {
			throw new InvalidationException("등록된 파일이 없습니다.");
		}

		attService.removeFile(properties.getUploadDir(), fileId);
	}

	@DeleteMapping("/svc/api/notice/{noticeId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable String noticeId) {
		noticeService.deleteNoticeInfo(noticeId);
	}

	@PostMapping("/svc/api/notice/{noticeId}")
	public NoticeVO update(NoticeVO notice, @PathVariable String noticeId, @RequestPart(name = "file", required = false) MultipartFile[] files) {
		notice.setNoticeId(noticeId);
		return noticeService.updateNoticeInfo(notice, files);
	}
}
