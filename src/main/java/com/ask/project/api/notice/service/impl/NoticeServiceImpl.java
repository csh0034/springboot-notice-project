package com.ask.project.api.notice.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ask.project.api.notice.service.NoticeService;
import com.ask.project.api.notice.vo.NoticeVO;
import com.ask.project.common.util.pagination.CorePagination;
import com.ask.project.common.util.pagination.CorePaginationInfo;
import com.ask.project.common.util.pagination.CorePaginationParam;

@Service
public class NoticeServiceImpl implements NoticeService{

	@Autowired
	private NoticeMapper noticeMapper;

	@Override
	public CorePagination<NoticeVO> selectNoticeList(CorePaginationParam param, String title, Date beginDt, Date endDt) {

		long totalItems = noticeMapper.selectNoticeListCount(title, beginDt, endDt);

		CorePaginationInfo info = new CorePaginationInfo(param.getPage(), param.getItemsPerPage(), totalItems);

		List<NoticeVO> list = noticeMapper.selectNoticeList(title, beginDt, endDt, info.getBeginRowNum(), info.getEndRowNum(), totalItems);

		return new CorePagination<NoticeVO>(info, list);
	}
}
