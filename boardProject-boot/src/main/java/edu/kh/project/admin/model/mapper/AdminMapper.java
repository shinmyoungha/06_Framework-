package edu.kh.project.admin.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.member.model.dto.Member;

@Mapper
public interface AdminMapper {

   // 관리자 로그인
   Member login(String memberEmail);

Board maxReadCount();

Board maxLikeCount();

Board maxCommentCount();

}
