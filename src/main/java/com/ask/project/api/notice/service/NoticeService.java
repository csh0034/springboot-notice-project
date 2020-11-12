package com.ask.project.api.notice.service;

import java.util.Date;

import com.ask.project.api.notice.vo.NoticeVO;
import com.ask.project.common.util.pagination.CorePagination;
import com.ask.project.common.util.pagination.CorePaginationParam;

public interface NoticeService {

	CorePagination<NoticeVO> selectNoticeList(CorePaginationParam param, String title, Date beginDt, Date endDt);
}
