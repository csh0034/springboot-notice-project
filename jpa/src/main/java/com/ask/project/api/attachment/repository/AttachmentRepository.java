package com.ask.project.api.attachment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ask.project.api.attachment.domain.ComtAttachment;

public interface AttachmentRepository extends JpaRepository<ComtAttachment, String> {

	List<ComtAttachment> findByAttachmentGroupId(String attachmentGroupId);

	boolean existsByAttachmentGroupId(String attachmentGroupId);
}
