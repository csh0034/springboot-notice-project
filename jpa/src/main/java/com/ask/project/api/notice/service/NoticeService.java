package com.ask.project.api.notice.service;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ask.core.config.BasicProperties;
import com.ask.core.exception.InvalidationException;
import com.ask.core.exception.NotFoundException;
import com.ask.core.security.SecurityUser;
import com.ask.core.security.SecurityUtils;
import com.ask.core.util.CoreUtils.string;
import com.ask.project.api.attachment.domain.ComtAttachmentGroup;
import com.ask.project.api.attachment.service.AttachmentService;
import com.ask.project.api.notice.domain.ComtNotice;
import com.ask.project.api.notice.repository.NoticeRepository;
import com.ask.project.common.util.pagination.CorePagination;
import com.ask.project.common.util.pagination.CorePaginationInfo;
import com.ask.project.common.util.pagination.CorePaginationParam;

@Service
@Transactional
public class NoticeService {

	@Autowired
	private NoticeRepository noticeRepository;

	@Autowired
	private AttachmentService attService;

	@Autowired
	private SecurityUtils securityUtils;

	@Autowired
	private BasicProperties properties;

	public ComtNotice findNotice(String noticeId) {

		if (string.isBlank(noticeId)) {
			throw new InvalidationException("noticeId가 없습니다.");
		}

		ComtNotice dbNotice = noticeRepository.findById(noticeId).orElse(null);

		if (dbNotice == null) {
			throw new NotFoundException("등록된 공지사항이 없습니다.");
		}

		return dbNotice;
	}

	public ComtNotice findNoticeAndIncReadCnt(String noticeId) {

		if (string.isBlank(noticeId)) {
			throw new InvalidationException("noticeId가 없습니다.");
		}

		ComtNotice dbNotice = noticeRepository.findById(noticeId).orElse(null);

		if (dbNotice == null || !dbNotice.getCompleted()) {
			throw new NotFoundException("등록된 공지사항이 없습니다.");
		}

		dbNotice.setReadCnt(dbNotice.getReadCnt() + 1);

		return noticeRepository.save(dbNotice);
	}

	public CorePagination<ComtNotice> getNoticeList(CorePaginationParam param, String title, Date beginDt, Date endDt) {

		Page<ComtNotice> list = noticeRepository.searchAll(
						title,
						beginDt,
						endDt,
						PageRequest.of(
							param.getPage().intValue() - 1,
							param.getItemsPerPage().intValue()
						)
					);

		CorePaginationInfo info = new CorePaginationInfo(param.getPage(), param.getItemsPerPage(), list.getTotalElements());

		List<ComtNotice> notice = list.getContent();

		return new CorePagination<ComtNotice>(info, notice);
	}

	public void deleteNotice(String noticeId) {

		ComtNotice notice = this.findNotice(noticeId);

		SecurityUser user = securityUtils.getUser();

		if (!string.equals(user.getUserId(), notice.getCreatorId())) {
			throw new InvalidationException("권한이 없습니다.");
		}

		if (string.isNotBlank(notice.getAttachmentGroupId())) {
			attService.removeFiles_group(properties.getUploadDir(), notice.getAttachmentGroupId());
		}

		noticeRepository.deleteById(noticeId);
	}

	public ComtNotice updateNotice(ComtNotice notice, MultipartFile[] files) {

		Date now = new Date();
		SecurityUser user = securityUtils.getUser();

		ComtNotice dbNotice = this.findNotice(notice.getNoticeId());

		if (!string.equals(user.getUserId(), dbNotice.getCreatorId())) {
			throw new InvalidationException("권한이 없습니다.");
		}

		if (string.isBlank(notice.getTitle())) {
			throw new InvalidationException("제목을 입력 해주세요.");
		}
		if (string.isBlank(notice.getContent())) {
			throw new InvalidationException("내용을 입력 해주세요.");
		}

		notice.setReadCnt(dbNotice.getReadCnt());
		notice.setCompleted(true);

		if (!dbNotice.getCompleted()) {
			notice.setCreatorId(user.getUserId());
			notice.setCreatorNm(user.getUserNm());
			notice.setCreatedDt(now);
		} else {
			notice.setCreatorId(dbNotice.getCreatorId());
			notice.setCreatorNm(dbNotice.getCreatorNm());
			notice.setCreatedDt(dbNotice.getCreatedDt());
		}

		notice.setUpdaterId(user.getUserId());
		notice.setUpdaterNm(user.getUserNm());
		notice.setUpdatedDt(now);

		long maxFileSize = 30 * 1024 * 1024;
		String attGroupId = dbNotice.getAttachmentGroupId();

		if (files != null && files.length > 0) {
			if (string.isBlank(attGroupId)) {
				ComtAttachmentGroup attGroup = attService.uploadFiles_toNewGroup(properties.getUploadDir(), files, null, maxFileSize);
				attGroupId = attGroup.getAttachmentGroupId();
			} else {
				attService.uploadFiles_toGroup(properties.getUploadDir(), attGroupId, files, null, maxFileSize);
			}
		}
		notice.setAttachmentGroupId(attGroupId);

		return noticeRepository.save(notice);
	}

	public ComtNotice insertNoticeInfo() {

		Date now = new Date();
		SecurityUser user = securityUtils.getUser();

		ComtNotice notice = new ComtNotice();
		notice.setReadCnt(0L);
		notice.setCompleted(false);
		notice.setCreatorId(user.getUserId());
		notice.setCreatorNm(user.getUserNm());
		notice.setCreatedDt(now);
		notice.setUpdaterId(user.getUserId());
		notice.setUpdaterNm(user.getUserNm());
		notice.setUpdatedDt(now);

		return noticeRepository.save(notice);
	}

}
