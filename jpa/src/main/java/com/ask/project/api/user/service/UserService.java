package com.ask.project.api.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ask.core.exception.InvalidationException;
import com.ask.core.util.CoreUtils;
import com.ask.core.util.CoreUtils.string;
import com.ask.project.api.user.domain.ComtUser;
import com.ask.project.api.user.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public ComtUser selectUserInfo(String loginId) {
		return userRepository.findByLoginId(loginId).orElse(null);
	}

	public void login(String loginId, String password) {

		if (string.isBlank(loginId)) {
			throw new InvalidationException("아이디를 입력하세요.");
		}
		if (string.isBlank(password)) {
			throw new InvalidationException("비밀번호를를 입력하세요.");
		}

		ComtUser dbUser = userRepository.findByLoginId(loginId).orElse(null);

		if (dbUser == null || !CoreUtils.password.compare(password, dbUser.getPassword())) {
			throw new InvalidationException("아이디 또는 패스워드가 유효하지 않습니다.");
		}
	}
}
