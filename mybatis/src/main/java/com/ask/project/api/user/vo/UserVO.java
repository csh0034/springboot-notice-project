package com.ask.project.api.user.vo;

import com.ask.core.security.SecurityUser;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class UserVO extends SecurityUser {

	private static final long serialVersionUID = 2144358115726549515L;

	private Boolean enabled;
}
