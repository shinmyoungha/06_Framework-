package edu.kh.todo.model.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.kh.todo.model.mapper.TodoMapper;

@Repository // DAO 계층 역할 명시 + Bean 등록
public class TodoDAO {
	
	@Autowired // 의존성주입(DI) -> 같은 타입 + 상속관계 Bean 을 의존성 주입(DI)
	private TodoMapper mapper; // mapper에는 todoMapper의 구현체가 의존성 주입됨
							   // 그 구현체가 sqlSessionTemplate 이용

	public String testTitle() {
		return mapper.testTitle();
	}

}
