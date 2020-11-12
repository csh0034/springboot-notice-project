package com.ask.core.security;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SecurityRememberToken implements Serializable {

	private static final long serialVersionUID = -8356764301582602085L;

	private String loginId;
	private String series;
	private String token;
	private Date updatedDt;
}
