package edu.kh.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
					
@EnableScheduling // 해당 프로젝스 서버 실행 시 스케줄러를 활성화하여 이용하겠다!
@SpringBootApplication(exclude={SecurityAutoConfiguration.class}) // Spring Security 의 자동 설정 중 로그인페이지 이용 안함!
public class BoardProjectBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardProjectBootApplication.class, args);
	}

}
