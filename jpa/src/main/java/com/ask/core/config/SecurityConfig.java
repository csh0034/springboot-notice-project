package com.ask.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.ask.core.security.AjaxSessionTimeoutFilter;
import com.ask.core.security.CustomAccessDeniedHandler;
import com.ask.core.security.CustomFailureHandler;
import com.ask.core.security.CustomSuccessHandler;
import com.ask.core.security.CustomTokenRepository;
import com.ask.core.security.CustomUserDetailService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/assets/**", "/favicon.ico");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.addFilterAfter(ajaxSessionTimeoutFilter(), ExceptionTranslationFilter.class)
			.csrf().disable().headers().frameOptions().disable()
			.and().authorizeRequests()
				.antMatchers("/svc/**").hasRole("USER")
				.anyRequest().permitAll()
			.and().formLogin()
				.loginPage("/security/login")
				.loginProcessingUrl("/security/check")
				.successHandler(customSuccessHandler("/"))
				.failureHandler(customFailureHandler("/security/login"))
			.and().logout()
				.logoutUrl("/security/logout")
				.logoutSuccessUrl("/")
			.and().rememberMe()
				.key("ask")
				.tokenValiditySeconds(604800)
				.rememberMeCookieName("remember")
				.rememberMeParameter("remember")
				.tokenRepository(customTokenRepository())
				.authenticationSuccessHandler(customSuccessHandler("/"))
			.and().exceptionHandling()
				.accessDeniedHandler(customAccessDeniedHandler());
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
	}

	@Bean
	public AjaxSessionTimeoutFilter ajaxSessionTimeoutFilter() {
		return new AjaxSessionTimeoutFilter();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomUserDetailService();
	}

    @Bean
    public AuthenticationSuccessHandler	customSuccessHandler(String defaultTargetUrl) {
    	return new CustomSuccessHandler(defaultTargetUrl);
    }

    @Bean
    public AuthenticationFailureHandler customFailureHandler(String defaultFailureUrl) {
    	return new CustomFailureHandler(defaultFailureUrl);
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
    	return new CustomAccessDeniedHandler();
    }

    @Bean
    public PersistentTokenRepository customTokenRepository() {
    	return new CustomTokenRepository();
    }
}