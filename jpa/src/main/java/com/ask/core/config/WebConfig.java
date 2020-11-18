package com.ask.core.config;

import java.nio.charset.Charset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.ask.core.logger.LoggerInterceptor;
import com.ask.core.resolver.BooleanArgumentResolver;
import com.ask.core.resolver.DateArgumentResolver;

@Configuration
@EnableAspectJAutoProxy
public class WebConfig implements WebMvcConfigurer{

	@Autowired
	private LoggerInterceptor loggerInterceptor;

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {

		// 루트 페이지
		registry.addViewController("/").setViewName("redirect:/screen/notice/index");

		// 로그인 페이지
		registry.addViewController("/security/login").setViewName("security/login");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		// 로그 인터셉터
		registry.addInterceptor(loggerInterceptor)
				.addPathPatterns("/**")
				.excludePathPatterns("/", "/assets/**", "/error", "/favicon.ico");
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new BooleanArgumentResolver());
		argumentResolvers.add(new DateArgumentResolver());
	}

	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver cmr = new CommonsMultipartResolver();
		cmr.setMaxUploadSize(-1);
		cmr.setMaxUploadSizePerFile(-1);
		cmr.setMaxInMemorySize(1024 * 1024);
		return cmr;
	}

	@Bean
	public ViewResolver viewResolver() {
		BeanNameViewResolver viewResolver = new BeanNameViewResolver();
		viewResolver.setOrder(1);
		return viewResolver;
	}

	@Bean("jsonView")
	public MappingJackson2JsonView jsonView() {
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		jsonView.setExtractValueFromSingleKeyModel(true);
		jsonView.setPrettyPrint(true);
		return jsonView;
	}

	@Bean
	public HttpMessageConverter<?> jsonConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setDefaultCharset(Charset.forName("UTF-8"));

		return converter;
	}
}
