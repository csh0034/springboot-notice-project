package com.ask.project.api.attachment.service.impl;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ask.project.api.attachment.vo.AttachmentGroupVO;
import com.ask.project.api.attachment.vo.AttachmentVO;

@Mapper
public interface AttachmentMapper {

	void insertComtAttachmentGroup(AttachmentGroupVO attachmentGroup);

	void deleteComtAttachmentGroup(String attachmentGroupId);

	AttachmentGroupVO selectComtAttachmentGroup(String attachmentGroupId);

	void insertComtAttachments(@Param("list") List<AttachmentVO> list);

    void deleteComtAttachment(String attachmentId);

	void updateComtAttachments_removed(String attachmentGroupId);

	void updateComtAttachment_removed(String attachmentId);

	void increaseDownloadCnt(String attachmentId);

	List<AttachmentVO> selectComtAttachments(String attachmentGroupId);

    AttachmentVO selectComtAttachment(String attachmentId);

	long selectComtAttachmentGroupFileSize(String attachmentGroupId);

	boolean existsComtAttachments(String attachmentGroupId);
}
