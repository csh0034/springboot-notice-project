package com.ask.project.api.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ask.project.api.user.domain.ComtRemember;

public interface RememberRepository extends JpaRepository<ComtRemember, String> {

	void deleteByLoginId(String loginId);
}
