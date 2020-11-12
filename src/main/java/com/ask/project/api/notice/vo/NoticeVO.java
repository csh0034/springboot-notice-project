package com.ask.project.api.notice.vo;

import com.ask.project.common.vo.BaseVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NoticeVO extends BaseVO {

	private static final long serialVersionUID = 7250442712580960622L;

	private String noticeId;
	private String title;
	private String content;
	private String attachmentGroupId;
	private Long readCnt;
	private Boolean completed;

}
