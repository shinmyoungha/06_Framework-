package edu.kh.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("example") //    /example로 시작하는 주소를 해당 컨트로럴에 매핑
@Slf4j // lombok 라이브러리가 제공하는 로그 객체 자동생성 어노테이션
public class ExampleController {
	
	/* Model (org.springframework.ui.Model;)
	 * - Spring 에서 데이터 전달 역할을 하는 객체
	 * 
	 * - 기본 scope : request
	 * 
	 * - @SessionAttibute 와 함께 사용 시 session scope 변환
	 * 
	 * [기본 사용법]
	 * model.addAttribute("key", value);
	 * 
	 * */
	
	@GetMapping("ex1") //  /example/em1 GET 방식 요청 매핑
	public String ex1(HttpServletRequest req, Model model) {
		
		// Servlet 내장객체 범위
		// page < request < session < application
		req.setAttribute("test1", "HttpServletRequest로 전달한 값");
		model.addAttribute("test2", "Model로 전달한 값");
		
		// 단일 값 (숫자, 문자열) Model 을 이용해서 html 로 전달
		model.addAttribute("productName", "종이컵");
		model.addAttribute("price", 2000);
		
		// 복수 값(배열, List) Model 이용해서 html 전달
		List<String> fruitList = new ArrayList<>();
		fruitList.add("사과");
		fruitList.add("딸기");
		fruitList.add("바나나");
		
		model.addAttribute("fruitList", fruitList);
		
		// src/main/resources/templates/example/ex1.html 로 forward
		return "example/ex1";
	}
	
	
	
}
