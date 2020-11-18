package com.ask.project.api.notice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ask.project.api.notice.domain.ComtNotice;

public interface NoticeRepository extends JpaRepository<ComtNotice, String> {

	Page<ComtNotice> findByTitleContaining(String title, Pageable pageable);
}
