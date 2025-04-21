package edu.kh.project.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;

@Transactional(rollbackFor = Exception.class)
@Service // 비지니스로직 처리 역할 명시 + Bean 등록
@Slf4j
public class MemberServiceImpl implements MemberService {

	// 등록된 Bean 중에서 MemberMapper 와 같은 타입 or 상속관계인 Bean 을 찾아
	@Autowired 	// 의존성 주입(DI)
	private MemberMapper mapper;
	
	// Bcrypt 암호화 객체 의존성 주입(DI) - SecurityConfig 참고
	@Autowired
	private BCryptPasswordEncoder bcrypt;

	// 로그인 서비스
	@Override
	public Member login(Member inputMember) {

		// 암호화 진행
		
		// bcrypt.encode(문자열) : 문자열을 암호화하여 반환
		// String bcryptPassword = bcrypt.encode(inputMember.getMemberPw());
		// log.debug("bcryptPassword : " + bcryptPassword);
		
		// bcrypt.matches(평문, 암호화) : 평문과 암호화가 일치하면 true, 아니면 false 반환
		
		// 1. 이메일이 일치하면서 탈퇴하지 않은 회원 조회
		Member loginMember = mapper.login(inputMember.getMemberEmail());
		
		// 2. 만약에 일치하는 이메일이 없어서 조회 결과가 null 인 경우
		if(loginMember == null) return null;
		
		// 3. 입력받은 비밀번호(평문 : inputMember.getMemberPw()) 와
		//    암호화된 비밀번호(loginMember.getMemberPw())
		//	  두 비밀번호가 일치하는지 확인 (bcrypt.matches(평문, 암호화))
		// 일치하지 않으면
		if( !bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) {
			return null;
		}
		
		// 로그인 결과에서 비밀번호 제거
		loginMember.setMemberPw(null);
		
		return loginMember;
	}
}
