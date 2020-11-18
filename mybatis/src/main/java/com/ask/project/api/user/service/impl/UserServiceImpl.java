package com.ask.project.api.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ask.core.exception.InvalidationException;
import com.ask.core.util.CoreUtils.password;
import com.ask.core.util.CoreUtils.string;
import com.ask.project.api.user.service.UserService;
import com.ask.project.api.user.vo.UserVO;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Override
	public UserVO selectUserInfo(String loginId) {
		return userMapper.selectUserInfo(loginId);
	}

	@Override
	public String selectUserAuthority(String userId) {
		return userMapper.selectUserAuthority(userId);
	}

	@Override
	public void login(UserVO userVO) {

		if (string.isBlank(userVO.getLoginId())) {
			throw new InvalidationException("아이디를 입력하세요.");
		}
		if (string.isBlank(userVO.getPassword())) {
			throw new InvalidationException("비밀번호를를 입력하세요.");
		}

		UserVO dbUser = userMapper.selectUserInfo(userVO.getLoginId());

		if (dbUser == null || !password.compare(userVO.getPassword(), dbUser.getPassword())) {
			throw new InvalidationException("아이디 또는 패스워드가 유효하지 않습니다.");
		}
	}
}
