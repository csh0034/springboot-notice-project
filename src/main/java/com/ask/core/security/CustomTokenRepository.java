package com.ask.core.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.ask.project.api.user.service.impl.UserMapper;

public class CustomTokenRepository implements PersistentTokenRepository{

	@Autowired
	private UserMapper userMapper;

	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		userMapper.upsertUserTokenInfo(token.getUsername(), token.getSeries(), token.getTokenValue(), token.getDate());
	}

	@Override
	public void updateToken(String series, String tokenValue, Date lastUsed) {
		userMapper.updateUserTokenInfo(series, tokenValue, lastUsed);
	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {

		SecurityRememberToken token = userMapper.selectUserTokenInfo(seriesId);

		if (token == null) {
			return null;
		}

		return new PersistentRememberMeToken(
				token.getLoginId(),
				token.getSeries(),
				token.getToken(),
				token.getUpdatedDt());
	}

	@Override
	public void removeUserTokens(String username) {
		userMapper.deleteUserTokenInfo(username);
	}
}
