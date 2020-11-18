package com.ask.project.api.user.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ask.project.api.user.service.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/api/user/login")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void login(String loginId, String password) {
		userService.login(loginId, password);
	}
}
