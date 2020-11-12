package com.ask.core.security;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.ask.core.util.CoreUtils;
import com.ask.core.util.CoreUtils.string;

@Component
public class SecurityUtils {

	private SecurityUser testUser;

	public SecurityUser getUser() {
		SecurityUser user = SecurityUtils.session.getUserInfo();
		return (user != null) ? user : (testUser != null) ? testUser : null;
	}

	public SecurityUser getAnonymousUser() {
		return new SecurityUser();
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		SecurityUser user = getUser();
		return user == null ? new ArrayList<>() : user.getAuthorities();
	}

	public Collection<String> getStringAuthorities() {
		SecurityUser user = getUser();
		return user == null ? new ArrayList<>() : user.getStringAuthorities();
	}

	public boolean isAuthenticated() {
		return getUser() != null;
	}

	public void setTestUser() {
		SecurityUser user = new SecurityUser();
		user.setLoginId("testUser");
		user.setUserId("test-user-id");
		user.setUserNm("testUserNm");
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("USER"));
		user.setAuthorities(authorities);

		testUser = user;
	}

	public boolean hasAnyRole(String... roles) {
		Collection<? extends GrantedAuthority> authorities = this.getAuthorities();
		if (authorities == null || authorities.size() == 0) {
			return false;
		}
		for (String role : roles) {
			for (GrantedAuthority au : authorities) {
				if (string.equals(au.getAuthority(), role)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasRole(String role) {
		Collection<? extends GrantedAuthority> authorities = this.getAuthorities();
		if (authorities == null || authorities.size() == 0) {
			return false;
		}
		for (GrantedAuthority au : authorities) {
			if (string.equals(au.getAuthority(), role)) {
				return true;
			}
		}
		return false;
	}

	public static class session {
		public static final String SESSION_ID_USER_INFO = "--SecurityUtils.session.security-user";

		public static void invalidate() {
			HttpSession session = CoreUtils.session.getSession(true);
			session.invalidate();
		}

		public static void setUserInfo(SecurityUser userInfo) {
			HttpSession session = CoreUtils.session.getSession(true);
			session.setAttribute(SESSION_ID_USER_INFO, userInfo);
		}

		public static SecurityUser getUserInfo() {
			HttpSession session = CoreUtils.session.getSession(true);
			SecurityUser user = (SecurityUser) session.getAttribute(SESSION_ID_USER_INFO);
			return user;
		}
	}

}
