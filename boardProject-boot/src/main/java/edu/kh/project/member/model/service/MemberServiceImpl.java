package edu.kh.project.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;

@Transactional(rollbackFor = Exception.class)
@Service // 비즈니스로직 처리 역할 명시 + Bean 등록
@Slf4j
public class MemberServiceImpl implements MemberService{
	
	// 등록된 Bean 중에서 MemberMapper와 같은 타입 or 상속관계인 Bean을 찾아
	@Autowired // 의존성 주입(DI)
	private MemberMapper mapper;
	
	// Bcrypt 암호화 객체 의존성 주입(DI) - SecurityConfig 참고
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	// 로그인 서비스
	@Override
	public Member login(Member inputMember) {
		
		// 암호화 진행
		
		// bcrypt.encode(문자열) : 문자열을 암호화하여 반환
		//String bcryptPassword = bcrypt.encode(inputMember.getMemberPw());
		//log.debug("bcryptPassword : " + bcryptPassword);
		
		// bcrypt.matches(평문, 암호화): 평문과 암호화가 일치하면 true, 아니면 false 반환
		
		// 1. 이메일이 일치하면서 탈퇴하지 않은 회원 조회
		Member loginMember = mapper.login(inputMember.getMemberEmail());
		
		// 2. 만약에 일치하는 이메일이 없어서 조회 결과가 null 인 경우
		if(loginMember == null) return null;
		
		// 3. 입력받은 비밀번호(평문 :inputMember.getMemberPw()) 와
		//	 암호화된 비밀번호(loginMember.getMemberPw()) 
		//   두 비밀번호가 일치하는지 확인 (bcrypt.matches(평문, 암호화))
		// 일치하지 않으면
		if( !bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw()) ) {
			return null;
		}
		
		// 로그인 결과에서 비밀번호 제거
		loginMember.setMemberPw(null);
		
		return loginMember;
	}
	
	// 이메일 중복 검사 서비스
	@Override
	public int checkEmail(String memberEmail) {
		return mapper.checkEmail(memberEmail);
	}
	
	// 닉네임 중복 검사 서비스
	@Override
	public int checkNickname(String memberNickname) {
		return mapper.checkNickname(memberNickname);
	}
	
	// 회원가입 서비스
	@Override
	public int signup(Member inputMember, String[] memberAddress) {
		
		// 주소가 입력되지 않으면
		// inputMember.getMemberAddress() -> ",,"
		// memberAddress -> [,,]
		
		// 주소가 입력된 경우
		if(!inputMember.getMemberAddress().equals(",,")) {
			
			// String.join("구분자", 배열)
			// -> 배열의 모든 요소 사이에 "구분자"를 추가하여
			// 하나의 문자열로 만들어 반환하는 메서드
			String address = String.join("^^^", memberAddress);
			// [12345, 서울시 중구 남대문로, 3층, E강의장]
			// -> "12345^^^서울시 중구 남대문로^^^3층, E강의장"
			
			// 구분자로 "^^^" 쓴 이유 :
			// -> 주소, 상세주소에 안쓰일것같은 특수문자 작성
			// -> 나중에 마이페이지에서 주소 부분 수정시
			// -> DB에 저장된 기존 주소를 화면상에 출력해줘야함
			// -> 다시 3분할 해야할 때 구분자로 ^^^ 이용할 예정
			// -> 왜? 구분자가 기본 형태인 , 작성되어있으면
			// -> 주소, 상세주소에 , 가 들어오는경우 
			// -> 3분할이 아니라 N분할이 될 수 있기 때문에
			
			// inputMember의 memberAddress로 합쳐진 주소를 세팅
			inputMember.setMemberAddress(address);
			
		} else {
			// 주소가 입력되지 않은 경우
			inputMember.setMemberAddress(null); // null 로 저장
		}
		
		
		// 비밀번호 암호화 진행
		
		// inputMember 안의 memberPw -> 평문
		// 비밀번호를 암호화하여 inputMember 세팅
		String encPw = bcrypt.encode(inputMember.getMemberPw());
		inputMember.setMemberPw(encPw);
		
		// 회원 가입 mapper 메서드 호출
		return mapper.signup(inputMember);
		
	}
	
	
	
}
