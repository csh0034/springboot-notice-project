package com.ask.project.api.attachment.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.ask.core.util.CoreUtils.date;
import com.ask.project.common.util.IdGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "COMT_ATTACHMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComtAttachment implements Serializable {

	private static final long serialVersionUID = -6857959464455392669L;

	@Id
	@GenericGenerator(
		name = "attIdGenerator",
		strategy = "com.ask.project.common.util.IdGenerator",
		parameters = @Parameter(name = IdGenerator.PARAM_KEY, value = "att-")
	)
	@GeneratedValue(generator = "attIdGenerator")
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
