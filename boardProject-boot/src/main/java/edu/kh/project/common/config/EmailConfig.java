package edu.kh.project.common.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@PropertySource("classpath:/config.properties")
public class EmailConfig {
	/*
		SMTP (Simple Mail Transfer Protocol) : 이메일을 전송하는데 사용되는 표준 통신 프로토콜 동작 방식
		1. Spring boot 어플리케이션에서 내가 이메일 보냄
		2. Google SMTP 서버로 전송 (Google 서버가 중간 배달자 역할)
		3. Google SMTP 서버가 이메일주소에 맞는 "수신자" 에게 이메일 서버
		(gmail, yahoo, 회사 이메일 서버..)를 통해 발송
		4. 최종 수신자 이메일 받음
	
		(참고)기본 포트 25를 사용하지만, 보안 강화한 포트 587이나 465가 자주 사용됨
 	* 
 	* */
	
	// @Value : properties 에 작성된 내용 중 key 가 일치하는 value 값을 얻어와 필드에 대입
	@Value("${spring.mail.username}")
	private String userName;
	
	@Value("${spring.mail.password}")
	private String password;
	
	@Bean
	public JavaMailSender javaMailSender () {
		/*
		 * Spring 에서 JavaMailSender를 구성하는 Bean 을 정의하기위한 메서드.
		 * 
		 * JavaMailSender : 이메일을 보내는데 사용되는 인터페이스로,
		 * JavaMailSenderImpl 클래스를 통해 구현됨.
		 * SMTP 서버를 사용하여 이메일을 보내기 위한 구성을 제공.
		 * 
		 * */
		
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

		Properties prop = new Properties();
		
		prop.setProperty("mail.transport.protocol", "smtp"); // 전송 프로토콜을 설정. 여기서는 SMTP를 사용
		prop.setProperty("mail.smtp.auth", "true"); // SMTP 서버 인증을 사용할지 여부를 설정함.
													// true로 설정되어 있으므로 인증이 사용됨
													// SMTP 서버를 사용하여 이메일을 보내려면 보안 상의 이유로 인증이 필요.
													// (사용자이름(이메일)과 비밀번호(앱비밀번호) 확인)
		prop.setProperty("mail.smtp.starttls.enable", "true"); // STARTTLS를 사용하여 안전한 연결을 활성화할지 여부를 설정
		prop.setProperty("mail.debug", "true"); 		// 디버그 모드를 설정
		prop.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com"); // 신뢰할 수 있는 SMTP 서버 호스트를 지정
		prop.setProperty("mail.smtp.ssl.protocols", "TLSv1.2"); // SSL 프로토콜을 설정. 여기서는 TLSv1.2를 사용
		
		
		mailSender.setUsername(userName); // 이메일 계정의 사용자
		mailSender.setPassword(password); // 이메일 계정의 비밀번호
		mailSender.setHost("smtp.gmail.com"); // SMTP 서버 호스트를 설정.  Gmail의 SMTP 서버인 "smtp.gmail.com"을 사용
		mailSender.setPort(587); 			// SMTP 서버의 포트 587로 설정
		mailSender.setDefaultEncoding("UTF-8"); // 기본 인코딩을 설정
		mailSender.setJavaMailProperties(prop); // JavaMail의 속성을 설정(앞서 정의해둔 prop 있는 설정들을 여기에 추가)
		
		return mailSender;
		// 위처럼 각종 설정이 적용된 JavaMailSender 를 Bean 으로 등록하여
		// Spring 애플리케이션에서 이메일을 보내기 위한 구성을 제공함.
		// 필요한 곳에서 의존성 주업빋아 사용하면 된다!
	}
	
}
