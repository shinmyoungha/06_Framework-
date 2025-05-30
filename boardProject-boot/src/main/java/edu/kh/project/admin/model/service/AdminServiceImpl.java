package edu.kh.project.admin.model.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.admin.model.mapper.AdminMapper;
import edu.kh.project.board.model.dto.Board;
import edu.kh.project.member.model.dto.Member;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

	private final AdminMapper mapper;
	private final BCryptPasswordEncoder bcrypt;

	// 관리자 로그인
	@Override
	public Member login(Member inputMember) {

		Member loginMember = mapper.login(inputMember.getMemberEmail());

		if (loginMember == null) {
			return null;
		}

		if (!bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) {
			return null;
		}

		loginMember.setMemberPw(null);
		return loginMember;

	}

	// 최대 조회수 게시글
	@Override
	public Board maxReadCount() {
		return mapper.maxReadCount();
	}

	// 최대 좋아요수 게시글
	@Override
	public Board maxLikeCount() {
		return mapper.maxLikeCount();
	}

	// 최대 댓글수 게시글
	@Override
	public Board maxCommentCount() {
		return mapper.maxCommentCount();
	}

	// 새로운 회원 조회
	@Override
	public List<Member> getNewMember() {
		return mapper.getNewMember();
	}
	
	// 탈퇴 회원 조회
	@Override
	public List<Member> selectWithdrawnMemberList() {
		return mapper.selectWithdrawnMemberList();
	}
	
	// 삭제된 게시글 목록 조회
	@Override
	public List<Board> selectDeleteBoardList() {
		return mapper.selectDeleteBoardList();
	}

	// 탈퇴 회원 복구
	@Override
	public int restoreMember(int memberNo) {
		return mapper.restoreMember(memberNo);
	}
	
	// 삭제된 게시글 복구
	@Override
	public int restoreBoard(int boardNo) {
		return mapper.restoreBoard(boardNo);
	}
}