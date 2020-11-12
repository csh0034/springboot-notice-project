package com.ask.project.api.notice.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ask.project.api.notice.vo.NoticeVO;

@Mapper
public interface NoticeMapper {

	long selectNoticeListCount(
			@Param("title") 	String title,
			@Param("beginDt")	Date createdDt,
			@Param("endDt") 	Date endDt);

	List<NoticeVO> selectNoticeList(
			@Param("title") 		String title,
			@Param("beginDt") 		Date beginDt,
			@Param("endDt") 		Date endDt,
			@Param("beginRowNum") 	Long beginRowNum,
			@Param("endRowNum") 	Long endRowNum,
			@Param("totalItems") 	Long totalItems);

}
