package edu.kh.project.admin.model.service;

import edu.kh.project.member.model.dto.Member;

public interface AdminService {

   // 관리자 로그인
   Member login(Member inputMember);

}
