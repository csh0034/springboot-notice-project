package com.ask.project.support;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mac")
public class TestControllerSupport {

	private static final String TEST_USERNAME = "user1";
	private static final String TEST_PASSWORD = "springboot!";

	protected HttpHeaders headers = new HttpHeaders();
	protected MockHttpSession session;

	@Autowired
	protected MockMvc mvc;

	@BeforeEach
	public void setup() throws Exception {

		RequestBuilder builder = MockMvcRequestBuilders.post("/security/check")
				.param("username", TEST_USERNAME)
				.param("password", TEST_PASSWORD);

		MvcResult  mvcResult = this.mvc.perform(builder).andExpect(status().isFound()).andReturn();
		headers.set("X-Requested-With", "XMLHttpRequest");

		session = (MockHttpSession) mvcResult.getRequest().getSession();
    }

	public MockHttpServletRequestBuilder getMethodBuilder(String url) {
		return MockMvcRequestBuilders.get(url).session(session).headers(headers);
	}

	public MockHttpServletRequestBuilder postMethodBuilder(String url) {
		return MockMvcRequestBuilders.post(url).session(session).headers(headers);
	}

	public MockHttpServletRequestBuilder putMethodBuilder(String url) {
		return MockMvcRequestBuilders.put(url).session(session).headers(headers);
	}

	public MockHttpServletRequestBuilder deleteMethodBuilder(String url) {
		return MockMvcRequestBuilders.delete(url).session(session).headers(headers);
	}

	public MockHttpServletRequestBuilder multipartMethodBuilder(String url, MockMultipartFile file) {
		return MockMvcRequestBuilders.multipart(url).file(file).session(session).headers(headers);
	}
}
