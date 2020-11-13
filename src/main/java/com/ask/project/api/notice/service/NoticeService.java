package com.ask.project.api.notice.service;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import com.ask.project.api.notice.vo.NoticeVO;
import com.ask.project.common.util.pagination.CorePagination;
import com.ask.project.common.util.pagination.CorePaginationParam;

public interface NoticeService {

	NoticeVO selectNoticeInfo(String noticeId);

	NoticeVO selectNoticeInfoAndIncReadCnt(String noticeId);

	CorePagination<NoticeVO> selectNoticeList(CorePaginationParam param, String title, Date beginDt, Date endDt);

	void deleteNoticeInfo(String noticeId);

	NoticeVO updateNoticeInfo(NoticeVO notice, MultipartFile[] files);

	NoticeVO insertNoticeInfo();
}
