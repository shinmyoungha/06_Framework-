package edu.kh.project.board.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.Pagination;
import edu.kh.project.board.model.mapper.BoardMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardMapper mapper;

	// 게시판 종류 조회 서비스
	@Override
	public List<Map<String, Object>> selectBoardTypeList() {
		return mapper.selectBoardTypeList();
	}

	
	@Override
	public Map<String, Object> selectBoardList(Object boardCode, int cp) {
		
		// 1. 지정된 게시판(boadrCode)에서
		// 	  삭제되지 않은 게시글 수를 조회
		int listCount = mapper.getListCount(boardCode);
		
		// 2. 1번의 결과 + cp 를 이용해서
		// Pagination 객체를 생성
		// * Pagination 객체 : 게시글 목록 구성에 필요한 값을 저장한 객체
		Pagination pagination = new Pagination(cp, listCount);
		
		// 3. 특정 게시판의 지정된 페이지 목록 조회
		/*
		 * ROWBOUNDS 객체 (MyBatis 제공 객체)
		 * : 지정된 크기만큼 건너 뛰고(offset)
		 * 제한된 크기만큼(limit)의 행을 조회하는 객체
		 * 
		 * 
		 * */
		int limit = pagination.getLimit(); // 10개씩 조회
		int offset = (cp - 1) * limit;	
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		// Mapper 메서드 호출 시 원래 전달 할 수 있는 매개변수 1개
		// -> 2개를 전달 할 수 있는 경우가 있음
		// rowBounds 를 이용할 때!
		// -> 첫번째 매개변수 -> SQL 에 전달할 파라미터
		// -> 두번쨰 매개변수 -> RowBounds 객체 전달
		List<Board> boardList = mapper.selectBoardList(boardCode, rowBounds);	
		
		log.debug("boardList 결과 : {}", boardList);
		
		// 4. 목록 조회 결과 + Pagination 객체를 Map 으로 묶어서 반환
		Map<String, Object> map = new HashMap<>();
		
		map.put("pagination", pagination);
		map.put("boardList", boardList);
		
		// 5, 결과 반환
		return map;
		
	}
}
	