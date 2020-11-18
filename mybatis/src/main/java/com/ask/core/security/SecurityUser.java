package com.ask.core.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SecurityUser implements Serializable{

	private static final long serialVersionUID = -7448426492480316804L;
	protected String userId;
	protected String loginId;
	protected String password;
	protected String userNm;
	protected Collection<? extends GrantedAuthority> authorities;

	protected Collection<String> stringAuthorities;

	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;

		stringAuthorities = new ArrayList<>();
		for (GrantedAuthority authority: authorities) {
			stringAuthorities.add(authority.toString());
		}
	}
}
