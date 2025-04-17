package edu.kh.todo.model.service;

import java.util.Map;

import edu.kh.todo.model.dto.Todo;

public interface TodoService {

	/** (TEST) todoNo가 1인 할 일 제목 조회
	 * @return title
	 */
	String testTitle();

	/** 할 일 목록 + 완료된 할 일 갯수 조회
	 * @return map
	 */
	Map<String, Object> selectAll();

	/** 할 일 추가
	 * @param todoTitle
	 * @param todoContent
	 * @return result
	 */
	int addTodo(String todoTitle, String todoContent);

	/** 할 일 상세조회
	 * @param todoNo
	 * @return todo
	 */
	Todo todoDetail(int todoNo);

}
