package edu.kh.demo.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getter + setter + toString
@NoArgsConstructor // 기본생성자
public class MemberDTO {
	private String memberId;
	private String memberPw;
	private String memberName;
	private int    memberAge;
}
