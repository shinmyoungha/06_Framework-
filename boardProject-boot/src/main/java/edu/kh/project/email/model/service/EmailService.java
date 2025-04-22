package edu.kh.project.email.model.service;

public interface EmailService {

	/** 이메일 보내기 서비스
	 * @param string 무슨 이메일을 발송할지 구분할 key로 쓰임
	 * @param email : 수신자 이메일
	 * @return authKey (인증번호)
	 */
	String sendEmail(String string, String email);

}
