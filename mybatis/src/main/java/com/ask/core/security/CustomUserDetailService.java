package com.ask.core.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ask.project.api.user.service.impl.UserMapper;

public class CustomUserDetailService implements UserDetailsService{

	@Autowired
	private UserMapper userMapper;

	@Override
	public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

		SecurityUser user = userMapper.selectUserInfo(loginId);

		Optional.ofNullable(user).orElseThrow(() -> new UsernameNotFoundException("NOT_FOUND"));

		String authority = userMapper.selectUserAuthority(user.getUserId());

		return new User(loginId, user.getPassword(), AuthorityUtils.createAuthorityList(authority));
	}
}
