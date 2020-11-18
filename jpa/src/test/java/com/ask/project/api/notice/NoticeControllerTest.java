package com.ask.project.api.notice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import com.ask.core.util.CoreUtils;
import com.ask.core.util.CoreUtils.json;
import com.ask.project.api.attachment.domain.ComtAttachment;
import com.ask.project.api.notice.domain.ComtNotice;
import com.ask.project.support.TestControllerSupport;

public class NoticeControllerTest extends TestControllerSupport {

	public final Logger log = LoggerFactory.getLogger(this.getClass());

	@Test
	public void test() throws Exception {

		log.info("======================================= 1. list =======================================");
		RequestBuilder builder = getMethodBuilder("/api/notice")
							.param("title", "게시글")
							.param("itemsPerPage", "20");

		MvcResult mvcResult = this.mvc.perform(builder)
								.andExpect(status().isOk())
								.andExpect(jsonPath("$.page").value("1"))
								.andExpect(jsonPath("$.totalItems").value("3"))
								.andExpect(jsonPath("$.list[0].noticeId").value("notice-03"))
								.andReturn();

		assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());

		log.info("======================================= 2. update =======================================");
		MockMultipartFile file = new MockMultipartFile("file", "README.md", null, CoreUtils.file.getContent(new File("README.md")));

		builder = multipartMethodBuilder("/svc/api/notice/notice-01", file)
				.param("title", "타이틀 변경")
				.param("content", "내용 변경");

		mvcResult = this.mvc.perform(builder)
						.andExpect(status().isOk())
						.andReturn();

		ComtNotice notice = json.toObject(mvcResult.getResponse().getContentAsString(), ComtNotice.class);
		assertNotNull(notice);
		assertEquals(notice.getTitle(), "타이틀 변경");
		assertEquals(notice.getContent(), "내용 변경");


		log.info("======================================= 3. fileList =======================================");
		builder = getMethodBuilder("/api/notice/notice-01/files");

		mvcResult = this.mvc.perform(builder)
							.andExpect(status().isOk())
							.andExpect(jsonPath("$.[0].fileNm").value("README.md"))
							.andExpect(jsonPath("$.[0].creatorId").value("user-01"))
							.andReturn();

		assertEquals(mvcResult.getResponse().getContentType(), "application/json;charset=UTF-8");

		ComtAttachment[] attahcments = json.toObject(mvcResult.getResponse().getContentAsString(), ComtAttachment[].class);

		assertNotNull(attahcments);

		ComtAttachment attachment = attahcments[0];


		log.info("======================================= 4. fileDownload =======================================");
		builder = getMethodBuilder("/api/notice/notice-01/files/" + attachment.getAttachmentId());

		mvcResult = this.mvc.perform(builder)
							.andExpect(status().isOk())
							.andReturn();

		MockHttpServletResponse response = mvcResult.getResponse();

		assertEquals(response.getContentType(), "application/octet-stream");
		assertEquals(new Long(response.getContentLength()), attachment.getFileSize());


		log.info("======================================= 5. fileDelete =======================================");
		builder = deleteMethodBuilder("/svc/api/notice/notice-01/files/" + attachment.getAttachmentId());

		mvcResult = this.mvc.perform(builder)
							.andExpect(status().isNoContent())
							.andReturn();


		log.info("======================================= 6. delete =======================================");
		builder = deleteMethodBuilder("/svc/api/notice/notice-01");

		mvcResult = this.mvc.perform(builder)
							.andExpect(status().isNoContent())
							.andReturn();

	}
}
