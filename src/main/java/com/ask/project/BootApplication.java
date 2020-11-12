package com.ask.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.ask")
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
}
