package com.ask.project.api.user.service;

import com.ask.project.api.user.vo.UserVO;

public interface UserService {

	UserVO selectUserInfo(String loginId);

	String selectUserAuthority(String userId);

	void login(UserVO userVO);
}
