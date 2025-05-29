package edu.kh.project.admin.model.service;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.member.model.dto.Member;

public interface AdminService {

	// 관리자 로그인
	Member login(Member inputMember);

	/**
	 * 최대 조회수 게시글 조회
	 * @return
	 */
	Board maxReadCount();

	/**
	 * 최대 좋아요 게시글 조회
	 * @return
	 */
	Board maxLikeCount();

	/**
	 * 최대 댓글수 게시글 조회
	 * @return
	 */
	Board maxCommentCount();

}
