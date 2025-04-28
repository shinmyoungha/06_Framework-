package edu.kh.project.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller // 요청/응답 제어 역할 명시 + Bean 등록
public class MainController {
	
	@RequestMapping("/")  //  "/" 요청 매핑
	public String mainPage() {
		
		// forward 하겠다
		// 경로 :   src/main/resources(classpath:)/templates/common/main.html
		// 접두사 : src/main/resources(classpath:)/templates/  
		// 접미사 : .html
		return "common/main"; // 접두사와 접미사를 뺴고 써야함
	}
	
	// LoginFilter -> 로그인 안되어있을때 loginError 리다이렉트
	// -> message 만들어서 메인페이지로 리다이렉트
	@GetMapping("loginError")
	public String loginError(RedirectAttributes ra) {
		ra.addFlashAttribute("message", "로그인 후 이용해주세요.");
		return "redirect:/";
	}
}
