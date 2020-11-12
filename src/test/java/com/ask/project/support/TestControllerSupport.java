package com.ask.project.support;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.Cookie;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("windows")
public class TestControllerSupport {

	private static final String TEST_USERNAME = "user1";
	private static final String TEST_PASSWORD = "1234";

	protected HttpHeaders headers = new HttpHeaders();
	protected Cookie[] cookies = null;

	@Autowired
	protected MockMvc mvc;

	@BeforeEach
	public void setup() throws Exception {

		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		RequestBuilder builder = MockMvcRequestBuilders.post("/security/check")
				.param("username", TEST_USERNAME)
				.param("password", TEST_PASSWORD);

		MvcResult result = this.mvc.perform(builder).andExpect(status().isFound()).andReturn();
		cookies = result.getResponse().getCookies();
		headers.set("X-Requested-With", "XMLHttpRequest");
    }

	public MockHttpServletRequestBuilder getMethodBuilder(String url) {
		return MockMvcRequestBuilders.get(url).headers(headers).cookie(cookies);
	}

	public MockHttpServletRequestBuilder postMethodBuilder(String url) {
		return MockMvcRequestBuilders.post(url).headers(headers).cookie(cookies);
	}

	public MockHttpServletRequestBuilder putMethodBuilder(String url) {
		return MockMvcRequestBuilders.put(url).headers(headers).cookie(cookies);
	}

	public MockHttpServletRequestBuilder deleteMethodBuilder(String url) {
		return MockMvcRequestBuilders.delete(url).headers(headers).cookie(cookies);
	}

	public MockHttpServletRequestBuilder multipartMethodBuilder(String url, MockMultipartFile file) {
		return MockMvcRequestBuilders.multipart(url).file(file).headers(headers).cookie(cookies);
	}

	public MockHttpServletRequestBuilder putMultipartMethodBuilder(String url, MockMultipartFile file) {
		return MockMvcRequestBuilders.multipart(url).file(file).headers(headers).cookie(cookies).with(new RequestPostProcessor() {

			@Override
			public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
				request.setMethod("PUT");
				return request;
			}
		});
	}
}
