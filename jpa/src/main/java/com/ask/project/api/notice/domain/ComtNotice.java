package com.ask.project.api.notice.domain;

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
@Table(name = "COMT_NOTICE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComtNotice {

	@Id
	@GenericGenerator(
		name = "noticeIdGenerator",
		strategy = "com.ask.project.common.util.IdGenerator",
		parameters = @Parameter(name = IdGenerator.PARAM_KEY, value = "notice-")
	)
	@GeneratedValue(generator = "noticeIdGenerator")
	private String noticeId;

	private String title;

	private String content;

	private String attachmentGroupId;

	private Long readCnt;

	private Boolean completed;

	private String creatorId;

	private String creatorNm;

	private Date createdDt;

	private String updaterId;

	private String updaterNm;

	private Date updatedDt;

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
