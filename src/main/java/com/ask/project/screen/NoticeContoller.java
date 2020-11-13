package com.ask.project.screen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ask.core.exception.InvalidationException;
import com.ask.core.security.SecurityUser;
import com.ask.core.security.SecurityUtils;
import com.ask.core.util.CoreUtils.string;
import com.ask.project.api.notice.service.NoticeService;
import com.ask.project.api.notice.vo.NoticeVO;

@Controller("secreenNoticeController")
public class NoticeContoller {

	@Autowired
	private NoticeService noticeService;

	@Autowired
	private SecurityUtils securityUtils;

	@GetMapping("/screen/notice/index")
	public String index() {
		return "notice/index";
	}

	@GetMapping("/screen/notice/detail")
	public String detail(Model model, String noticeId) {

		NoticeVO notice = noticeService.selectNoticeInfoAndIncReadCnt(noticeId);

		boolean owner = false;

		if (securityUtils.isAuthenticated()) {

			SecurityUser user = securityUtils.getUser();

			if (string.equals(user.getUserId(), notice.getCreatorId())) {
				owner = true;
			}
		}

		model.addAttribute("notice", notice);
		model.addAttribute("owner", owner);

		return "notice/detail";
	}

	@GetMapping("/svc/screen/notice/add")
	public String add(Model model) {

		NoticeVO notice = noticeService.insertNoticeInfo();

		model.addAttribute("notice", notice);
		model.addAttribute("isModify", false);

		return "notice/modify";
	}

	@GetMapping("/svc/screen/notice/modify")
	public String modify(Model model, String noticeId) {

		NoticeVO notice = noticeService.selectNoticeInfo(noticeId);

		SecurityUser user = securityUtils.getUser();

		if (!string.equals(user.getUserId(), notice.getCreatorId())) {
			throw new InvalidationException("게시글 수정 권한이 없습니다.");
		}

		model.addAttribute("notice", notice);
		model.addAttribute("isModify", true);

		return "notice/modify";
	}
}
