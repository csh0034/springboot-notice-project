package com.ask.core.resolver;

import java.util.Date;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.ask.core.util.CoreUtils.string;

public class DateArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter param) {
        return Date.class.isAssignableFrom(param.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter param,
            ModelAndViewContainer mvc,
            NativeWebRequest request,
            WebDataBinderFactory binder) throws Exception {

        String text = request.getParameter(param.getParameterName());
        return string.toDate(text) ;
    }

}
