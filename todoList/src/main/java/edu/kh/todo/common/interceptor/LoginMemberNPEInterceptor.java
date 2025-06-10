package edu.kh.todo.common.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginMemberNPEInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false); // 세션이 없으면 null 반환

        // loginMember가 없는 경우
        if (session == null || session.getAttribute("loginMember") == null) {
        	
        	session.setAttribute("alertMsg", "로그아웃되었습니다.");
            response.sendRedirect("/"); // 루트로 리다이렉트
            return false; // 컨트롤러 진입 차단
        }

        return true; // 로그인 된 경우 요청 계속 진행
    }
}



