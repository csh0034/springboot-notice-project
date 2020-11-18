package com.ask.project.api.notice.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ask.project.api.notice.domain.ComtNotice;

public interface NoticeRepositoryCustom {

	Page<ComtNotice> searchAll(String title, Date beginDt, Date endDt, Pageable pageable);
}
