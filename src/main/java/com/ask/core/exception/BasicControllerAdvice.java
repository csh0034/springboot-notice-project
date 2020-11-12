package com.ask.core.exception;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.ask.core.util.CoreUtils;
import com.ask.core.util.CoreUtils.string;

@ControllerAdvice
public class BasicControllerAdvice {

	@ExceptionHandler(Exception.class)
	public ModelAndView exception(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (CoreUtils.webutils.isAjax(request) || string.equalsIgnoreCase(request.getParameter("responseType"), "json")) {
			return processAjax(e);
		}

		ModelAndView mv = new ModelAndView("/error");

		if (e instanceof InvalidationException) {
			mv.addObject("message", ((InvalidationException) e).getExceptionMessage().getMessage());
		} else if (e instanceof InvalidationsException) {
			mv.addObject("messageList", ((InvalidationsException) e).getExceptionMessages());
		} else {
			mv.addObject("message", "에러 발생");
		}

		return mv;
	}

	private ModelAndView processAjax(Exception e) {

		MappingJackson2JsonView view = new MappingJackson2JsonView();
		view.setPrettyPrint(true);
		view.setExtractValueFromSingleKeyModel(true);

		ModelAndView jsonView = new ModelAndView(view);

		if (e instanceof InvalidationException) {

			InvalidationException ie = (InvalidationException)e;
			ResponseMessage rm = ie.getExceptionMessage();

			jsonView.setStatus(HttpStatus.valueOf(rm.getStatus()));
			jsonView.addObject(rm);

		} else if (e instanceof InvalidationsException) {

			InvalidationsException ies = (InvalidationsException)e;
			List<ResponseMessage> rms = ies.getExceptionMessages();

			if (rms != null && rms.size() > 0) {
				jsonView.setStatus(HttpStatus.valueOf(rms.get(0).getStatus()));
			} else {
				jsonView.setStatus(HttpStatus.BAD_REQUEST);
			}

			jsonView.addObject(rms);

		} else {
			jsonView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			jsonView.addObject(ResponseMessage.error("서버 오류가 발생하여 작업을 중단하였습니다."));
		}

		return jsonView;
	}
}
