package com.ask.project.api.attachment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ask.project.api.attachment.domain.ComtAttachmentGroup;

public interface AttachmentGroupRepository extends JpaRepository<ComtAttachmentGroup, String> {

}
