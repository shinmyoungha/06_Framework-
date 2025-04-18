package edu.kh.todo.cotroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.service.TodoService;

@Controller
@RequestMapping("todo") //  "/todo" 로 시작하는 모든 요청 매핑
public class TodoController {

	@Autowired // 의존성 주입(같은 타입 + 상속관계인 Bean 을 의존성 주입:DI)
	private TodoService service;
	
	@PostMapping("add") //  /todo/add 로 Post 방식 요청 매핑
	public String addTodo(@RequestParam("todoTitle") String todoTitle,
						  @RequestParam("todoContent") String todoContent,
						  RedirectAttributes ra) {
		
		// RedirectAttributes : 리다이렉트 시 값을 1회성으로 전달하는 객체
		// RedirectAttributes.addFlashAttribute("key"m value) 형식으로 세팅.
		// -> request scope -> session scope 로 잠시 변환
		
		// 응답 전 : request scope
		// redirect : session scope 로 이동 -> 사용
		// 응답까지 끝나고 난 후 : request scope 복귀
		
		// 서비스 매서드 호출 후 결과 받기
		int result = service.addTodo(todoTitle, todoContent);	
		
		// 결과에 따라 message 값 지정
		String message = null;
		
		if(result > 0) message = "할 일 추가 성공!!!";
		else           message = "할 일 추가 실패...";
		
		// 리다이렉트 후 1회성으로 사용할 데이터를 속성으로 추가(req -> session -> req)
		ra.addFlashAttribute("message", message);

		return "redirect:/"; // 메인페이지로 재요청
	}
	
	// /todo/detail?todoNo=1
	// /todo/detail?todoNo=2
	@GetMapping("detail") //  /todo/detail GET 방식 요청 매핑
	public String todoDetail(@RequestParam("todoNo") int todoNo,
							  Model model,
							  RedirectAttributes ra) {
		
		Todo todo = service.todoDetail(todoNo);
		
		String path = null;
		
		// 조회결과가 있을 경우 detail.html forward
		if(todo != null) {
			
			// templates/todo/detail.html
			path = "todo/detail";
			
			model.addAttribute("todo", todo); // request scope 값 세팅
			
		} else {
			// 조회결과가 없을 경우 메인페이지로 redirect
			path = "redirect:/";
			
			ra.addFlashAttribute("message", "해당 할 일이 존재하지 않습니다");
			
		}
		 
		return path;
	}

	// /todo/changeComplete?todoNo=4&complete=Y
	/** 완료 여부 변경
	 * @param todo : 커맨드 객체(@ModelAttribute 생략)
	 * 			   - 파라미터의 key와 Todo 객체의 필드명이 일치하면
	 * 			   - 일치하는 필드값이 파라미터의 value값으로 세팅된 상태
	 * 			   - 즉, todoNo 와 complete 두 필드가 세팅 완료된 상태
	 * @return
	 */
	@GetMapping("changeComplete")
	public String changeComplete(/*@ModelAttribute*/ Todo todo,
								   RedirectAttributes ra){
		
		// 변경 서비스 호출
		int result = service.changeComplete(todo);
		
		// 변경 성공 시 : "변경 성공!!"
		// 변경 실패 시 : "변경 실패.."
		String message = null;
		if(result > 0) message = "변경 성공!!";
		else		   message = "변경 실패..";
		
		ra.addFlashAttribute("message", message);
		
		// 상대경로 (현재 위치 중요!!!)
		// 현재 주소 : /todo/changeComplete
		// 재요청 주소 : /todo/detail
		
		return "redirect:detail?todoNo="+ todo.getTodoNo();
	}
	
	/** 수정 화면 전환 요청
	 * @return
	 */
	@GetMapping("update")
	public String todoUpdate(@RequestParam("todoNo")  int todoNo,
							 Model model) {
		
		// 상세 조회 서비스 호출(재활용) -> 수정화면에 출력할 기존내용
		Todo todo = service.todoDetail(todoNo);
		
		model.addAttribute("todo", todo);
		
		// classpath:/templates/todo/update.html 로 forward
		return "todo/update";
	}
	
	@PostMapping("update")
	public String todoUpdate(Todo todo, RedirectAttributes ra) {
		
		// 수정 서비스 호출 후 결과 반환받기
		int result = service.todoUpdate(todo);
		
		String path = "redirect:";
		String message = null;
		
		if(result > 0) {
			// 상세 조회로 리다이렉트
			path += "/todo/detail?todoNo=" + todo.getTodoNo();
			// redirect:/todo/detail?todoNo=1
			message = "수정 성공!!";
			
		} else {
			
			// 다시 수정 화면 리다이렉트
			path += "/todo/update?todoNo=" +todo.getTodoNo();
			// redirect:/todo/detail?todoNo=1
			message = "수정 실패..";
		}
		
		ra.addFlashAttribute("message", message);
		
		return path;
	}
	
	@GetMapping("delete")
	public String todoDelete(@RequestParam("todoNo") int todoNo,
							 RedirectAttributes ra) {
		
		int result = service.todoDelete(todoNo);
		
		String path = null;
		String message = null;
		
		if(result > 0) {
			path = "/";
			message = "삭제 성공";
			
		} else {
			path = "/todo/detail?todoNo=" + todoNo;
			message = "삭제 실패";
			
		} 
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" +path;
	}
}
