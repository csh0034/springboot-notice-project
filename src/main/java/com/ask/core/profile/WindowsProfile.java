package com.ask.core.profile;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.ask.core.config.BasicProperties;

@Configuration
@Profile("windows")
public class WindowsProfile {

	@Bean
	public BasicProperties basicProperties() {
		return BasicProperties.builder()
					.profile("widnows")
					.uploadDir("c:/springboot/data/upload")
					.build();
	}
}
