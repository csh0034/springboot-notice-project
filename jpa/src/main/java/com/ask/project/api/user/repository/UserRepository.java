package com.ask.project.api.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ask.project.api.user.domain.ComtUser;

public interface UserRepository extends JpaRepository<ComtUser, String> {

	Optional<ComtUser> findByLoginId(String loginId);
}
