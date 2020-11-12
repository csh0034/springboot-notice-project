package com.ask.core.logger;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class LoggerAspect {

	@Before("execution(* com.ask.project.api.*.service.impl.*Impl.*(..))")
	public void startLog(JoinPoint jp) {
	}

	@Around("execution(* com.ask.project.api.*.service.impl.*Impl.*(..))")
	public Object timeLog(ProceedingJoinPoint pjp) throws Throwable {
		long startTime = System.currentTimeMillis();

		log.info("1: " + Arrays.toString(pjp.getArgs()));
		log.info("2: " + pjp.getSignature().getName());

		Object result = pjp.proceed();

		long endTime = System.currentTimeMillis();
		log.info(pjp.getSignature().getName() + " - 메서드 실행시간 : " + (endTime - startTime) + " ms");

		return result;
	}
}