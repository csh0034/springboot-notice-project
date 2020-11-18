package com.ask.project.api.attachment.vo;

import java.io.Serializable;
import java.util.Date;

import com.ask.core.util.CoreUtils.date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AttachmentVO implements Serializable {

	private static final long serialVersionUID = -6857959464455392669L;

	private String attachmentId;
	private String attachmentGroupId;
	private String fileNm;
	private String contentType;
	private Long fileSize;
	private String savedFilePath;
	private Long downloadCnt;
	private Boolean fileDeleted;

	private String creatorId;
	private Date createdDt;
	private String updaterId;
	private Date updatedDt;
	private String creatorNm;
	private String updaterNm;

	public String getCreatedDe() {
		return this.createdDt == null ? "" : date.format(this.createdDt, "yyyy-MM-dd");
	}

	public String getCreatedDtime() {
		return this.createdDt == null ? "" : date.format(this.createdDt, "yyyy-MM-dd HH:mm");
	}

	public String getUpdatedDe() {
		return this.updatedDt == null ? "" : date.format(this.updatedDt, "yyyy-MM-dd");
	}

	public String getUpdatedDtime() {
		return this.updatedDt == null ? "" : date.format(this.updatedDt, "yyyy-MM-dd HH:mm");
	}

}
