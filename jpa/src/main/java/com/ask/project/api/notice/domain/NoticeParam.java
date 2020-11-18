package com.ask.project.api.notice.domain;

import java.io.Serializable;
import java.util.Date;

import com.ask.core.util.CoreUtils.date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class NoticeParam implements Serializable {

	private static final long serialVersionUID = 5116365768820360137L;

	private Long page;
	private Long itemsPerPage;
	private String title;
	private Date beginDt;
	private Date endDt;

	public String getBeginDe() {
		return this.beginDt == null ? "" : date.format(this.beginDt, "yyyy-MM-dd");
	}

	public String getEndDe() {
		return this.endDt == null ? "" : date.format(this.endDt, "yyyy-MM-dd");
	}
}
