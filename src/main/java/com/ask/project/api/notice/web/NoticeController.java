package com.ask.project.api.notice.web;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ask.project.api.notice.service.NoticeService;
import com.ask.project.api.notice.vo.NoticeVO;
import com.ask.project.common.util.pagination.CorePagination;
import com.ask.project.common.util.pagination.CorePaginationParam;

@RestController
public class NoticeController {

	@Autowired
	private NoticeService noticeService;

	@GetMapping("/api/notice")
	public CorePagination<NoticeVO> list(CorePaginationParam param, String title, Date beginDt, Date endDt) {
		return noticeService.selectNoticeList(param, title, beginDt, endDt);
	}
}
