package com.ask.core.error;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.ask.core.exception.ResponseMessage;
import com.ask.core.util.CoreUtils.webutils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class CustomErrorController implements ErrorController {

	private static final String ERROR_MAPPING_PATH = "/error";
	private static final String ERROR_PAGE_PATH = "error";

	@Override
	public String getErrorPath() {
		return ERROR_MAPPING_PATH;
	}

	@RequestMapping(ERROR_MAPPING_PATH)
	public ModelAndView handleError(HttpServletRequest request, HttpServletResponse response, Model model) {

		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

		HttpStatus httpStatus = (status != null) ? HttpStatus.valueOf(Integer.valueOf(status.toString())) : HttpStatus.INTERNAL_SERVER_ERROR;

		if (!webutils.isAjax(request)) {
			log.warn("httpStatus : " + httpStatus.toString());
			model.addAttribute("httpStatus", httpStatus.toString());
			model.addAttribute("msg", httpStatus.getReasonPhrase());
		}

		switch (httpStatus.value()) {
		case 403:
			return new ModelAndView(ERROR_PAGE_PATH + "/403");
		case 404:

			if (webutils.isAjax(request)) {

				MappingJackson2JsonView view = new MappingJackson2JsonView();
				view.setPrettyPrint(true);
				view.setExtractValueFromSingleKeyModel(true);
				ModelAndView jsonView = new ModelAndView(view);

				jsonView.addObject(ResponseMessage.notFound("404 NOT FOUND"));

				return jsonView;

			} else {
				return new ModelAndView(ERROR_PAGE_PATH + "/404");
			}
		case 500:
			return new ModelAndView(ERROR_PAGE_PATH + "/500");
		default:
			return new ModelAndView(ERROR_PAGE_PATH + "/error");
		}

	}
}
