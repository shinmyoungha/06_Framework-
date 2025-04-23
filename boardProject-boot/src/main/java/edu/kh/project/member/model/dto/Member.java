package edu.kh.project.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO(Data Transfer Object)
// - 데이터 전달용 객체
// - DB에서 조회된 결과를 담을 용도 또는
//   SQL 구문에 사용할 값을 전달하는 용도로
//	 관련성 있는 데이터를 한번에 묶어서 다룸

@Data // Getter + Setter + toString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
	
	private int memberNo;			// 회원 번호
	private String memberEmail;		// 회원 이메일
	private String memberPw;		// 회원 비밀번호
	private String memberNickname;	// 회원 닉네임
	private String memberTel;		// 회원 전화번호
	private String memberAddress;	// 회원 주소
	private String profileImg;		// 회원 프로필 이미지
	private String enrollDate;		// 회원 가입일 
	private String memberDelFl;		// 회원 탈퇴 여부(Y/N)
	private int authority;			// 권한(1:일반사용자,2:관리자)
}
