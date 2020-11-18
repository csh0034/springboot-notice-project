package com.ask.core.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.ask.project.api.user.domain.ComtRemember;
import com.ask.project.api.user.repository.RememberRepository;

public class CustomTokenRepository implements PersistentTokenRepository {

	@Autowired
	private RememberRepository rememberRepository;

	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		rememberRepository.save(new ComtRemember(token.getSeries(), token.getUsername(), token.getTokenValue(), token.getDate()));
	}

	@Override
	public void updateToken(String series, String tokenValue, Date lastUsed) {

		ComtRemember remember = new ComtRemember();
		remember.setSeries(series);
		remember.setToken(tokenValue);
		remember.setUpdatedDt(lastUsed);

		rememberRepository.save(remember);
	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {

		ComtRemember remember = rememberRepository.findById(seriesId).orElse(null);

		if (remember == null) {
			return null;
		}

		return new PersistentRememberMeToken(
				remember.getLoginId(),
				remember.getSeries(),
				remember.getToken(),
				remember.getUpdatedDt());
	}

	@Override
	public void removeUserTokens(String username) {
		rememberRepository.deleteByLoginId(username);
	}
}
