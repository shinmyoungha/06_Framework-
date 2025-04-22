package edu.kh.project.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@SessionAttributes({"loginMember"})
@Controller
@RequestMapping("member")
@Slf4j
public class MemberController {
	
	@Autowired
	private MemberService service;

	/* [로그인]
	 * - 특정 사이트에 아이디/비밀번호 등을 입력해서
	 *   해당 정보가 DB에 있으면 조회/서비스 이용
	 *   
	 * - 로그인 한 회원 정보는 session 에 기록하여
	 * 로그아웃 또는 브라우저 종료 시 까지
	 * 해당 정보를 계속 이용할 수 있게 함
	 *  
	 * */
	
	/** 로그인
	 * @param inputMember : 커맨드 객체 (@ModelAttribute 생략)
	 * 						memberEmail, memberPw 세팅된 상태
	 * @param ra : 리다이렉트 시 request scope -> session scope -> request 로 데이터 전달
	 * @param model : 데이터 전달용 객체(기본 request scope)
	 * 				/ (@SessionAttributes 어노테이션과 함께 사용 시 session scope 이동)
	 * 
	 * @return 
	 */
	@PostMapping("login")
	public String login(Member inputMember,
						RedirectAttributes ra,
						Model model,
						@RequestParam(value="saveId", required = false) String saveId,
						HttpServletResponse resp) {
		
		// 체크박스
		// - 체크가 된 경우 : "on"
		// - 체크가 안 된 경우 : null
		
		// 로그인 서비스 호출
		Member loginMember = service.login(inputMember);
		
		// 로그인 실패 시
		if(loginMember == null) {
			ra.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하니 않습니다.");
			
		} else { // 로그인 성공 시
			
			// session scope 에 loginMember 추가
			model.addAttribute("loginMember", loginMember);
			// 1단계 : model 을 이용하여 request scope 에 세팅됨
			// 2단계 : 클래스 위에 @SessionAttributes() 어노테이션 작성하여
            //		   session scope 로 loginMember를 이동
			
			// ************************* Cookie *************************
			// 로그인 시 작성한 이메일 저장 (쿠키에)
			
			// 쿠키 객체 생성(K:V)
			Cookie cookie = new Cookie("saveId", loginMember.getMemberEmail());
			// saveId = user01@kh.or.kr
			
			// 쿠키가 적용될 경로 설정
			// -> 클라이언트가 어떤 요청을 할 때 쿠키를 첨부할지 지정
			// ex) "/" : IP 또는 도메인 또는 localhost
			//			--> 메인 페이지 + 그 하위 주소 모두
			cookie.setPath("/");
			
			// 쿠키의 만료 기간 지정
			
			if(saveId != null) { // 아이디 저장 체크받스 체크했을 때
				cookie.setMaxAge(60 * 60 * 24 * 30 );// 30일 (초 단위로 지정)
				
			} else { // 체크 안 했을 때
				cookie.setMaxAge(0); // 0초 (클라이언트 쿠키 삭제)
				
			}
			
			// 응답 객체에 쿠키에 추가 -> 클라이언트에게 전달
			resp.addCookie(cookie);
		}
		
		
		return "redirect:/"; // 메인페이지 재요청
	}
	
	/** 로그아웃 : session 에 저장된 로그인된 회원 정보를 없앰
	 * @param SessionStatus : @SessionAttribute로 지정된 특정 속성을 세션에서 제거 기능 제공 객체
	 * @return
	 */
	@GetMapping("logout")
	public String logout(SessionStatus status) {
		
		status.setComplete(); // 세션을 완료 시킴 ( == @@SessionAttribute로 등록된 세션 개체 제거 기능 제공 객체)
		
		return "redirect:/";
	}
	
	/** 회원 가입 페이지로 이동
	 * @return
	 */
	@GetMapping("signup")
	public String signupPage() {
		return "member/signup";
	}
	
	/** 이메일 중복검사(비동기 요청)
	 * @return 중복된 데이터의 개수
	 */
	@ResponseBody // 응답 본문(fetch)으로 돌려보냄
	@GetMapping("checkEmail") // GET 방식 요청 /member/checkEmail
	public int checkEmail(@RequestParam("memberEmail") String memberEmail) {
		return service.checkEmail(memberEmail);
	}
}
