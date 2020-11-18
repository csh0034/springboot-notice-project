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
@Table(name = "COMT_ATTACHMENT_GROUP")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComtAttachmentGroup implements Serializable {

	private static final long serialVersionUID = 3750787542666129106L;

	@Id
	@GenericGenerator(
		name = "attGrpIdGenerator",
		strategy = "com.ask.project.common.util.IdGenerator",
		parameters = @Parameter(name = IdGenerator.PARAM_KEY, value = "att-")
	)
	@GeneratedValue(generator = "attGrpIdGenerator")
	private String attachmentGroupId;

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
