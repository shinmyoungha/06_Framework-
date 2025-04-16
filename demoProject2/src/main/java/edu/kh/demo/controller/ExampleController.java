package edu.kh.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.kh.demo.model.dto.Student;
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
		
		// DTO 객체 Model 을 이용해서 html로 전달
		Student std = new Student();
		std.setStudentNo("12345");
		std.setName("홍길동");
		std.setAge(22);
		
		model.addAttribute("std", std);
		
		// List<Student> 객체 Model 을 이용해서 html로 전달
		List<Student> stdList = new ArrayList<>();
		
		stdList.add(new Student("11111", "김일번", 20));
		stdList.add(new Student("22222", "최이번", 23));
		stdList.add(new Student("33333", "홍삼번", 22));
		
		model.addAttribute("stdList", stdList);
		
		// src/main/resources/templates/example/ex1.html 로 forward
		return "example/ex1";
	}
	
	@PostMapping("ex2") // /example/ex2 Post 방식 요청 매핑
	public String e2(Model model) {
		
		model.addAttribute("str", "<h1> 테스트 중 &times; </h1>");
		
		return "example/ex2";
	}
	
	@GetMapping("ex3")
	public String ex3(Model model) {
		
		model.addAttribute("key", "제목");
		model.addAttribute("query", "검색어");
		model.addAttribute("boardNo", 10);
		
		return "example/ex3";
	}
	
	@GetMapping("ex3/{path:[0-9]+}") // + 하면 10자리까지 나옴 11자리부터는 오류
	public String pathVariableTest(@PathVariable("path") int path ) {
		// conrtoller 에서 해야하는일 동일한 경우에
		// example/ex3/1 , example/ex3/2, example/ex3/3 ...
		// 주소 중 {path} 부분의 값을 가져와서 매개변수로 저장
		// conrtoller 단의 메서드에서 사용할 수 있도록 해줌
		// + request scope 자동 세팅
		
		log.debug("path : " + path);
		
		return "example/testResult";
	}
	
	@GetMapping("ex4")
	public String ex4(Model model) {
		
		Student std = new Student("67890", "잠만보", 22);
		
		model.addAttribute("std", std);
		model.addAttribute("num", 100);
		
		return "example/ex4";
	}
	
	@GetMapping("ex5")
	public String ex5(Model model) {
		
		model.addAttribute("massage", "타임리프 + Javascript 사용연습");
		model.addAttribute("num1", 12345);
		
		Student std = new Student();
		std.setStudentNo("22222");
		model.addAttribute("std", std);
		
		return "example/ex5";
	}
}
