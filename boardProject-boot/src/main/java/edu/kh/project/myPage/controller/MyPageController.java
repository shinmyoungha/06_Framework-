package edu.kh.project.myPage.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.dto.UploadFile;
import edu.kh.project.myPage.model.service.MyPageService;
import lombok.extern.slf4j.Slf4j;

/*
 * @SessionAttributes 의 역할
 *  - Model에 추가된 속성 중 key값이 일치하는 속성을 session scope로 변경
 *  - SessionStatus 이용 시 session에 등록된 완료할 대상을 찾는 용도
 * 
 * @SessionAttribute 의 역할 (매개변수에 쓰는 것)
 * - Session에 존재하는 값을 Key로 얻어오는 역할 
 * 
 * */

@SessionAttributes({"loginMember"})
@Controller
@RequestMapping("myPage")
@Slf4j
public class MyPageController {
	
	@Autowired
	private MyPageService service;
	
	@GetMapping("info")  // /myPage/info GET 요청 매핑
	public String info(@SessionAttribute("loginMember") Member loginMember,
						Model model) {
		
		// 현재 로그인한 회원의 주소를 꺼내옴
		// 현재 로그인한 회원 정보 -> session에 등록된 상태(loginMember)
		
		String memberAddress = loginMember.getMemberAddress();
		// "04540^^^서울 중구 남대문로 120^^^3층, E강의장"
		// 주소가 없다면 null
		
		// 주소가 있을 경우에만 동작
		if(memberAddress != null) {
			
			// 구분자 "^^^" 를 기준으로
			// memberAddress 값을 쪼개어 String[] 로 반환
			String[] arr = memberAddress.split("\\^\\^\\^");
			// -> "04540^^^서울 중구 남대문로 120^^^3층, E강의장"
			// -> ["04540", "서울 중구 남대문로 120", "3층, E강의장"]
			//	       0                1                   2
			
			model.addAttribute("postcode", arr[0]);
			model.addAttribute("address", arr[1]);
			model.addAttribute("detailAddress", arr[2]);
		}
		
		
		return "myPage/myPage-info";
	}
	
	// 프로필 이미지 변경 화면 이동
	@GetMapping("profile") // /myPage/profile GET 요청 매핑
	public String profile() {
		return "myPage/myPage-profile";
	}
	// 비밀번호 변경 화면 이동
	@GetMapping("changePw") // /myPage/changePw GET 요청 매핑
	public String changePw() {
		return "myPage/myPage-changePw";
	}
	// 회원 탈퇴 화면 이동
	@GetMapping("secession") // /myPage/secession GET 요청 매핑
	public String secession() {
		return "myPage/myPage-secession";
	}
	// 파일 업로드 테스트 화면 이동
	@GetMapping("fileTest") // /myPage/fileTest GET 요청 매핑
	public String fileTest() {
		return "myPage/myPage-fileTest";
	}
	
	
	/** 회원 정보 수정
	 * @param inputMember : 커맨드 객체(@ModelAttribute가 생략된 상태) 
	 * 						제출된 수정된 회원 닉네임, 전화번호, 주소
	 * @param loginMember : 로그인한 회원 정보 (회원 번호 사용할 예정)
	 * @param memberAddress : 주소만 따로 받은 String[] 구분자 ^^^ 변경 예정
	 * @param ra	: 
	 * @return
	 */
	@PostMapping("info")
	public String updateInfo(Member inputMember,
							@SessionAttribute("loginMember") Member loginMember,
							@RequestParam("memberAddress") String[] memberAddress,
							RedirectAttributes ra) {
		
		// inputMember에 로그인한 회원 번호 추가
		inputMember.setMemberNo(loginMember.getMemberNo());
		// inputMember : 회원 번호, 회원 닉네임, 전화번호, 주소
		
		// 회원 정보 수정 서비스 호출
		int result = service.updateInfo(inputMember, memberAddress);
		
		String message = null;
		
		if(result > 0) { // 회원 정보 수정 성공
			
			// loginMember 새로 세팅
			// 우리가 방금 바꾼 값으로 세팅
			
			// loginMember는 세션에 저장된 로그인한 회원 정보가 
			// 저장된 객체를 참조하고있다!
			
			// -> loginMember를 수정하면
			//    세션에 저장된 로그인한 회원 정보가 수정된다
			// == 세션 데이터와 DB 데이터를 동기화
			
			loginMember.setMemberNickname( inputMember.getMemberNickname() );
			loginMember.setMemberTel( inputMember.getMemberTel() );
			loginMember.setMemberAddress( inputMember.getMemberAddress() );
			
			message = "회원 정보 수정 성공!!";
			
		} else {
			
			message = "회원 정보 수정 실패..";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:info";
	}
	
	/** 비밀번호 변경
	 * @param paramMap : 모든 파라미터(요청 데이터)를 맵으로 저장
	 * @param loginMember : 세션에 등록된 현재 로그인한 회원 정보
	 * @param ra
	 * @return
	 */
	@PostMapping("changePw")  //  /myPage/changePw POST 요청 매핑
	public String changePw(@RequestParam Map<String, String> paramMap, 
							@SessionAttribute("loginMember") Member loginMember,
							RedirectAttributes ra) {
		// paramMap = {currentPw=asd123, newPw=pass02!, newPwConfirm=pass02!}
		
		// 로그인한 회원 번호
		int memberNo = loginMember.getMemberNo();
		
		// 현재 + 새 비번 + 회원번호를 서비스로 전달
		int result = service.changePw(paramMap, memberNo);
		
		String path = null;
		String message = null;
		
		if(result > 0) {
			// 변경 성공 시
			message = "비밀번호가 변경되었습니다!";
			path = "/myPage/info";
			
		} else {
			// 변경 실패 시
			message = "현재 비밀번호가 일치하지 않습니다";
			path = "/myPage/changePw";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
	}
	
	
	/** 회원 탈퇴
	 * @param memberPw		: 입력 받은 비밀번호
	 * @param loginMember   : 로그인한 회원 정보(세션)
	 * @param status : 세션 완료 용도의 객체 -> @SessionAttributes 로 등록된 세션을 완료
	 * @return
	 */
	@PostMapping("secession")
	public String secession(@RequestParam("memberPw") String memberPw,
							@SessionAttribute("loginMember") Member loginMember, 
							RedirectAttributes ra,
							SessionStatus status) {
		
		// 로그인한 회원의 회원번호 꺼내기
		int memberNo = loginMember.getMemberNo();
		
		// 서비스 호출 (입력받은 비밀번호, 로그인한 회원번호)
		int result = service.secession(memberPw, memberNo);
		
		String message = null;
		String path = null;
		
		if(result > 0) {
			message = "탈퇴 되었습니다.";
			path = "/";
			
			status.setComplete(); // 세션 완료 시킴
			
		} else {
			
			message = "비밀번호가 일치하지 않습니다";
			path = "secession";
		}
		
		ra.addFlashAttribute("message", message);
		
		// 탈퇴 성공 -> redirect:/   (메인페이지)
		// 탈퇴 실패 -> redirect:secession  (상대경로)
		//			->  /myPage/secession  (현재경로 POST)
		//			->  /myPage/secession  (GET 요청)
		return "redirect:" + path;
	}
	
	/* Spring에서 파일 업로드를 처리하는 방법
	 * 
	 * - encType = "multipart/form-data"로 클라이언트 요청을 받으면
	 *    (문자, 숫자, 파일 등이 섞여있는 요청)
	 *    
	 *   이를 MultipartResolver(FileConfig에 정의)를 이용해서 섞여있는 파라미터를 분리
	 *   
	 *   문자열, 숫자 -> String
	 *   파일		  -> MultipartFile
	 * 
	 * */
	@PostMapping("file/test1")  // /myPage/file/test1  POST 요청 매핑
	public String fileUpload1(@RequestParam("uploadFile") MultipartFile uploadFile,
							RedirectAttributes ra) throws Exception {
		
		String path = service.fileUpload1(uploadFile);
		// 웹에서 접근할 수 있는 경로를 반환
		// path = /myPage/file/A.jpg
		
		// 파일이 저장되어 웹에서 접근할 수 있는 경로가 반환되었을 때
		if( path != null ) {
			ra.addFlashAttribute("path", path);
		}
		
		
		return "redirect:/myPage/fileTest";
	}
	
	/** 업로드한 파일 DB 저장 + 서버 저장 + 조회
	 * @param uploadFile
	 * @param loginMember
	 * @param ra
	 * @return
	 * @throws Exception
	 */
	@PostMapping("file/test2")
	public String fileUpload2(@RequestParam("uploadFile") MultipartFile uploadFile,
							@SessionAttribute("loginMember") Member loginMember,
							RedirectAttributes ra) throws Exception {
		
		// 로그인한 회원의 번호 얻어오기(누가 업로드 했는가)
		int memberNo = loginMember.getMemberNo();
		
		// 업로드디된 파일 정보를 DB에 INSERT 후 결과 행의 개수 반환받기
		int result = service.fileUpload2(uploadFile, memberNo);
		
		String message = null;
		
		if(result > 0) {
			message = "파일 업로드 성공";
		} else {
			message = "파일 업로드 실패..";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:/myPage/fileTest";
	}
	
	
	/** 파일 목록 조회 화면 이동
	 * @param model
	 * @param loginMember : 현재 로그인한 회원의 번호가 필요! 
	 * @return
	 */
	@GetMapping("fileList")
	public String fileList(Model model, 
						@SessionAttribute("loginMember") Member loginMember) {
		
		// 파일 목록 조회 서비스 호출 (현재 로그인한 회원이 올린 이미지만 조회)
		int memberNo = loginMember.getMemberNo();
		List<UploadFile> list = service.fileList(memberNo);
		
		// model 에 list 담아서 forward
		model.addAttribute("list", list);
		
		return "myPage/myPage-fileList";
	}
	
	@PostMapping("file/test3")  // /myPage/file/test3
	public String fileUpload3(@RequestParam("aaa") List<MultipartFile> aaaList,
							@RequestParam("bbb") List<MultipartFile> bbbList,
							@SessionAttribute("loginMember") Member loginMember,
							RedirectAttributes ra) throws Exception {
		
		log.debug("aaaList : " + aaaList);
		log.debug("bbbList : " + bbbList);
		
		// aaa 파일 미제출 시
		// -> 0번, 1번 인덱스가 존재하는 List가 있음
		// -> 0,1번 인덱스에는 MultipartFile 객체가 존재하나, 둘다 비어있는 객체인 상태.
		// -> 0, 1번 인덱스가 존재하는 이유는 html에서 제출된 파라미터 중 name 값이 aaa인 2개
		
		// bbb 파일 미제출 시
		// -> 0번 인덱스에 있는 MultipartFile 객체가 비어있음
		
		// 여러 파일 업로드 서비스 호출
		int memberNo = loginMember.getMemberNo();
		
		int result = service.fileUpload3(aaaList, bbbList, memberNo);
		// result == 업로드된 파일 개수
		
		String message = null;
		
		if(result == 0) {
			message = "업로드된 파일이 없습니다.";
			
		} else {
			message = result + "개의 파일이 업로드 되었습니다!";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:/myPage/fileTest";
	}
	
	@PostMapping("profile")
	public String profile( @RequestParam("profileImg") MultipartFile profileImg,
							@SessionAttribute("loginMember") Member loginMember,
							RedirectAttributes ra) throws Exception {

	
		// 업로드된 파일 정보를 DB에 INSERT 후 결과 행의 갯수 반환 받을 예정
		int result = service.profile(profileImg, loginMember);
		
		String message = null;
		
		if(result > 0) message = "변경 성공!";
		else 			message = "변경 실패";
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:profile"; 
	}
	
	
	
	
	
	
	
}
