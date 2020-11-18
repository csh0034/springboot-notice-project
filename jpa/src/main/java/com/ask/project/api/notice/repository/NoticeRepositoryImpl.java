package com.ask.project.api.notice.repository;

import static com.ask.project.api.notice.domain.QComtNotice.comtNotice;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.ask.core.util.CoreUtils.date;
import com.ask.core.util.CoreUtils.string;
import com.ask.project.api.notice.domain.ComtNotice;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class NoticeRepositoryImpl implements NoticeRepositoryCustom {

	@Autowired
	private JPAQueryFactory queryFactory;

	@Override
	public Page<ComtNotice> searchAll(String title, Date beginDt, Date endDt, Pageable pageable) {

		BooleanBuilder builder = new BooleanBuilder();

		if (string.isNotBlank(title)) {
			builder.and(comtNotice.title.contains(title));
		}
		if (beginDt != null) {
			builder.and(comtNotice.createdDt.after(beginDt));
		}
		if (endDt != null) {
			builder.and(comtNotice.createdDt.before(date.addDays(endDt, 1)));
		}

		QueryResults<ComtNotice> result = queryFactory
										.selectFrom(comtNotice)
										.where(builder)
										.offset(pageable.getOffset())
										.limit(pageable.getPageSize())
										.orderBy(comtNotice.createdDt.desc())
										.fetchResults();

		return new PageImpl<>(result.getResults(), pageable, result.getTotal());
	}
}
