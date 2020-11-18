package com.ask.project.common.vo;

import java.io.Serializable;
import java.util.Date;

import com.ask.core.util.CoreUtils.date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BaseVO implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Long rowNum;
	protected String creatorId;
	protected Date createdDt;
	protected String updaterId;
	protected Date updatedDt;
	protected String creatorNm;
	protected String updaterNm;

	public String getCreatedDe() {
		return this.createdDt == null ? "" : date.format(this.createdDt, "yyyy-MM-dd");
	}

	public String getCreatedDtime() {
		return this.createdDt == null ? "" : date.format(this.createdDt, "yyyy-MM-dd HH:mm:ss");
	}

	public String getUpdatedDe() {
		return this.updatedDt == null ? "" : date.format(this.updatedDt, "yyyy-MM-dd");
	}

	public String getUpdatedDtime() {
		return this.updatedDt == null ? "" : date.format(this.updatedDt, "yyyy-MM-dd HH:mm:ss");
	}
}
