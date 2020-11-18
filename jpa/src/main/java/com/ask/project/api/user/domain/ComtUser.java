package com.ask.project.api.user.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.ask.project.common.util.IdGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "COMT_USER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComtUser {

	@Id
	@GenericGenerator(
		name = "userGrpIdGenerator",
		strategy = "com.ask.project.common.util.IdGenerator",
		parameters = @Parameter(name = IdGenerator.PARAM_KEY, value = "user-")
	)
	@GeneratedValue(generator = "userGrpIdGenerator")
	private String userId;

	@Column(unique = true)
	private String loginId;

	private String authority;

	private String password;

	private String userNm;

	private Boolean enabled;
}
