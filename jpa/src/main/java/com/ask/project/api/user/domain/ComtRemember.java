package com.ask.project.api.user.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "COMT_REMEMBER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComtRemember {

	@Id
	private String series;
	private String loginId;
	private String token;
	private Date updatedDt;
}
