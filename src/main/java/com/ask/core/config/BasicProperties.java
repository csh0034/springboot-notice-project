package com.ask.core.config;

import com.ask.core.util.CoreUtils.string;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BasicProperties {

	private String profile;
	private String uploadDir;

	public String getUploadDir() {
		return getDir(this.uploadDir);
	}

	private String getDir(String dir) {
		if (string.startsWith(dir, "~/")) {
			dir = System.getProperty("user.home") + "/" + string.substring(dir, 2);
		}
		return dir;
	}
}
