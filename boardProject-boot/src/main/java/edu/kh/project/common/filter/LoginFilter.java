package edu.kh.project.common.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*
 * Filter : 요청, 응답 시 걸러내거나 추가할 수 있는 객체
 * 
 * [필터 클래스 생성 방법]
 * 1. jakarta.servlet.Filter 인터페이스 상속받기
 * 2. doFilter() 메서드 오버라이딩
 * 
 * */

// 로그인이 되어있지 않은 경우 특정 페이지로 돌아가게 함
public class LoginFilter implements Filter{

	@Override
	public void doFilter(ServletRequest request,
						 ServletResponse response, 
						 FilterChain chain) throws IOException, ServletException {
		
		// ServletRequest : HttpServletRequest 의 부모 타입
		// ServletResponse : HttpServletResponse 의 부모 타입
		
		// seesion 필요함 -> 왜? -> loginMember 가 session 에 담김
		
		// Http 통신이 가능한 형태로 (자식형태) 다운 캐스팅
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		
		// ******* 계시판 부분 할 때 확인!!! *******
		// *****************************************
		
		// Session 객체 얻어오기
		HttpSession session = req.getSession();
		
		// 세션에서 로그인 한 회원 정보를 얻어옴
		// 얻어왔으나, 없을때 (null) -> 로그인이 되어있지 않은 상태
		
		if( session.getAttribute("loginMember") == null ) {
			
			// loninError 재요청
			resp.sendRedirect("/loginError");
			
		} else {
			// 로그인이 되어있는 상태
			
			// FilterChain
			// - 다음 필터 또는 DispatcherServlet 과 연결된 객체
			
			// 다음 필터로 요청/응답 객체 전달
			// 만약에 다음 필터가 없으면 DispatcherServlet 로 요청/응답 객체 전달
			chain.doFilter(request, response);
		}
	}
}
