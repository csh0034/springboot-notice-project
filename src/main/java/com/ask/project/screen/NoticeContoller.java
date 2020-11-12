package com.ask.project.screen;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("secreenNoticeController")
public class NoticeContoller {

	@GetMapping("/screen/notice/index")
	public String index() {
		return "notice/index";
	}

	@GetMapping("/screen/notice/detail")
	public String detail() {
		return "notice/detail";
	}

	@GetMapping("/svc/screen/notice/add")
	public String add() {
		return "notice/modify";
	}

	@GetMapping("/svc/screen/notice/modify")
	public String modify() {
		return "notice/modify";
	}
}
