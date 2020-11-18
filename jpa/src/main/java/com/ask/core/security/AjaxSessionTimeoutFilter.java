package com.ask.core.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import com.ask.core.util.CoreUtils.string;

public class AjaxSessionTimeoutFilter implements Filter {

	@Autowired
	private SecurityUtils securityUtils;

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (isAjaxRequest(req)) {
            try {
            	chain.doFilter(req, res);
            } catch (AuthenticationException e) {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            } catch (AccessDeniedException e) {
            	if (!securityUtils.isAuthenticated()) {
            		res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            	} else {
            		res.sendError(HttpServletResponse.SC_FORBIDDEN);
            	}
            }
        } else
            chain.doFilter(req, res);
    }

    private boolean isAjaxRequest(HttpServletRequest req) {
    	String header = req.getHeader("X-Requested-With");
        return string.equalsIgnoreCase(header, "XMLHttpRequest");
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }
}