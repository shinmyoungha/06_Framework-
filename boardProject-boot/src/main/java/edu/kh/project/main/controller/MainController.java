package edu.kh.project.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // 요청/응답 제어 역할 명시 + Bean 등록
public class MainController {
	
	@RequestMapping("/")  //  "/" 요청 매핑
	public String mainPage() {
		
		// forward 하겠다
		// 경로 :   src/main/resources(classpath:)/templates/common/main.html
		// 접두사 : src/main/resources(classpath:)/templates/  
		// 접미사 : .html
		return "common/main";
	}
}
