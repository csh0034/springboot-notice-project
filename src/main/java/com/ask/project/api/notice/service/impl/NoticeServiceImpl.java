package com.ask.project.api.notice.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ask.core.config.BasicProperties;
import com.ask.core.exception.InvalidationException;
import com.ask.core.security.SecurityUser;
import com.ask.core.security.SecurityUtils;
import com.ask.core.util.CoreUtils.string;
import com.ask.project.api.attachment.service.AttachmentService;
import com.ask.project.api.attachment.vo.AttachmentGroupVO;
import com.ask.project.api.notice.service.NoticeService;
import com.ask.project.api.notice.vo.NoticeVO;
import com.ask.project.common.util.pagination.CorePagination;
import com.ask.project.common.util.pagination.CorePaginationInfo;
import com.ask.project.common.util.pagination.CorePaginationParam;

@Service
public class NoticeServiceImpl implements NoticeService {

	@Autowired
	private NoticeMapper noticeMapper;

	@Autowired
	private AttachmentService attService;

	@Autowired
	private SecurityUtils securityUtils;

	@Autowired
	private BasicProperties properties;

	@Override
	public NoticeVO selectNoticeInfo(String noticeId) {

		if (string.isBlank(noticeId)) {
			throw new InvalidationException("noticeId가 없습니다.");
		}

		NoticeVO dbNotice = noticeMapper.selectNoticeInfo(noticeId);

		if (dbNotice == null) {
			throw new InvalidationException("등록된 공지사항이 없습니다.");
		}

		return dbNotice;
	}

	@Override
	public NoticeVO selectNoticeInfoAndIncReadCnt(String noticeId) {

		if (string.isBlank(noticeId)) {
			throw new InvalidationException("noticeId가 없습니다.");
		}

		NoticeVO dbNotice = noticeMapper.selectNoticeInfo(noticeId);

		if (dbNotice == null || !dbNotice.getCompleted()) {
			throw new InvalidationException("등록된 공지사항이 없습니다.");
		}

		noticeMapper.updateNoticeReadCnt(noticeId);

		return noticeMapper.selectNoticeInfo(noticeId);
	}

	@Override
	public CorePagination<NoticeVO> selectNoticeList(CorePaginationParam param, String title, Date beginDt, Date endDt) {

		long totalItems = noticeMapper.selectNoticeListCount(title, beginDt, endDt);

		CorePaginationInfo info = new CorePaginationInfo(param.getPage(), param.getItemsPerPage(), totalItems);

		List<NoticeVO> list = noticeMapper.selectNoticeList(title, beginDt, endDt, info.getBeginRowNum(), info.getEndRowNum(), totalItems);

		return new CorePagination<NoticeVO>(info, list);
	}

	@Override
	public void deleteNoticeInfo(String noticeId) {

		NoticeVO notice = this.selectNoticeInfo(noticeId);

		SecurityUser user = securityUtils.getUser();

		if (!string.equals(user.getUserId(), notice.getCreatorId())) {
			throw new InvalidationException("권한이 없습니다.");
		}

		if (string.isNotBlank(notice.getAttachmentGroupId())) {
			attService.removeFiles_group(properties.getUploadDir(), notice.getAttachmentGroupId());
		}

		noticeMapper.deleteNoticeInfo(noticeId);
	}

	@Override
	public NoticeVO updateNoticeInfo(NoticeVO notice, MultipartFile[] files) {

		Date now = new Date();
		SecurityUser user = securityUtils.getUser();

		NoticeVO dbNotice = this.selectNoticeInfo(notice.getNoticeId());

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
			notice.setCreatedDt(now);
		} else {
			notice.setCreatorId(dbNotice.getCreatorId());
			notice.setCreatedDt(dbNotice.getCreatedDt());
		}

		notice.setUpdaterId(user.getUserId());
		notice.setUpdatedDt(now);

		long maxFileSize = 30 * 1024 * 1024;
		String attGroupId = dbNotice.getAttachmentGroupId();

		if (files != null && files.length > 0) {
			if (string.isBlank(attGroupId)) {
				AttachmentGroupVO attGroup = attService.uploadFiles_toNewGroup(properties.getUploadDir(), files, null, maxFileSize);
				attGroupId = attGroup.getAttachmentGroupId();
			} else {
				attService.uploadFiles_toGroup(properties.getUploadDir(), attGroupId, files, null, maxFileSize);
			}
		}
		notice.setAttachmentGroupId(attGroupId);

		noticeMapper.updateNoticeInfo(notice);

		return noticeMapper.selectNoticeInfo(notice.getNoticeId());
	}

	@Override
	public NoticeVO insertNoticeInfo() {

		Date now = new Date();
		SecurityUser user = securityUtils.getUser();

		NoticeVO notice = new NoticeVO();
		notice.setNoticeId(string.getNewId("notice-"));
		notice.setReadCnt(0L);
		notice.setCompleted(false);
		notice.setCreatorId(user.getUserId());
		notice.setCreatedDt(now);
		notice.setUpdaterId(user.getUserId());
		notice.setUpdatedDt(now);

		noticeMapper.insertNoticeInfo(notice);

		return noticeMapper.selectNoticeInfo(notice.getNoticeId());
	}
}
