package com.ask.project.api.user.service.impl;

import java.util.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ask.core.security.SecurityRememberToken;
import com.ask.project.api.user.vo.UserVO;

@Mapper
public interface UserMapper {

	UserVO selectUserInfo(String loginId);

	String selectUserAuthority(String userId);

	void upsertUserTokenInfo(
			@Param("loginId") String loginId,
			@Param("series") String series,
			@Param("token") String token,
			@Param("updatedDt") Date updatedDt);

	void updateUserTokenInfo(
			@Param("series") String series,
			@Param("token") String token,
			@Param("updatedDt") Date updatedDt);

	SecurityRememberToken selectUserTokenInfo(String series);

	void deleteUserTokenInfo(String loginId);
}
