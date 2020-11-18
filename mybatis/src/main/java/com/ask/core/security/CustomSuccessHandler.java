package com.ask.core.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.ask.project.api.user.service.impl.UserMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private UserMapper userMapper;

	private String defaultUrl;

	public CustomSuccessHandler(String defaultTargetUrl) {
		this.defaultUrl = defaultTargetUrl;
	}

	@Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        SecurityUser user = userMapper.selectUserInfo(authentication.getName());

        user.setAuthorities(authentication.getAuthorities());
        SecurityUtils.session.setUserInfo(user);

        if (authentication instanceof RememberMeAuthenticationToken) {

        	log.info("RememberMe Login Success");

        	this.setDefaultTargetUrl(request.getRequestURL().toString());
        	this.setAlwaysUseDefaultTargetUrl(true);
        	super.handle(request, response, authentication);

        } else if (authentication instanceof UsernamePasswordAuthenticationToken) {

        	log.info("Form Login Success");

        	this.setDefaultTargetUrl(defaultUrl);
        	this.setAlwaysUseDefaultTargetUrl(false);
        	super.onAuthenticationSuccess(request, response, authentication);

        }
    }
}
