package com.ask.project;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ComponentScan("com.ask")
@Slf4j
public class BootApplication {

	@Bean
	public BootGracefulShutdown gracefulShutdown() {
		return new BootGracefulShutdown();
	}

	@Bean
	public ConfigurableServletWebServerFactory webServerFactory(final BootGracefulShutdown gracefulShutdown) {
		TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
		factory.addConnectorCustomizers(gracefulShutdown);
		return factory;
	}

	public static void main(String[] args) {
		SpringApplication.run(BootApplication.class, args);
	}

	//@Bean
	public CommandLineRunner run(ApplicationContext ctx) {
	    return (a) -> {

	    	String [] beanNames  = ctx.getBeanDefinitionNames();
	    	Arrays.sort(beanNames);
	    	for (String bean : beanNames) {
	    		log.info(bean);
			}
	    };
	}
}
