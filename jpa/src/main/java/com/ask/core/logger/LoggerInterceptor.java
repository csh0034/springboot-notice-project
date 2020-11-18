package com.ask.core.logger;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ask.core.util.CoreUtils.date;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggerInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


		log.info("====================================== START ======================================");

		SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);

		if (savedRequest != null) {
			log.info("savedRequest : " + savedRequest.getRedirectUrl());
		}

		log.info("Request Info : (" + request.getMethod() + ") - " + date.format(new Date(), "HH:mm:ss") + " - "
				+ request.getRequestURI());

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		log.info("======================================= END =======================================");
	}

}
