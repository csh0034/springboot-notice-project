package com.ask.core.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public 	class CustomFailureHandler implements AuthenticationFailureHandler {

	private String defaultFailureUrl;

	public CustomFailureHandler(String defaultFailureUrl) {
		this.defaultFailureUrl = defaultFailureUrl;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {

		log.warn("authentication fail : " + e.getClass().getSimpleName());

		// 폼 로그인 시 계정정보 에러
		if (e instanceof BadCredentialsException) {
			request.getSession().setAttribute("error", "badCredentials");
        }

		response.sendRedirect(this.defaultFailureUrl);
	}
}
