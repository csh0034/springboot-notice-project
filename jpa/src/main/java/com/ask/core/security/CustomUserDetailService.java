package com.ask.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ask.project.api.user.domain.ComtUser;
import com.ask.project.api.user.repository.UserRepository;

public class CustomUserDetailService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
		ComtUser user = userRepository.findByLoginId(loginId).orElseThrow(() -> new UsernameNotFoundException("NOT_FOUND"));
		return new User(loginId, user.getPassword(), AuthorityUtils.createAuthorityList(user.getAuthority()));
	}
}
