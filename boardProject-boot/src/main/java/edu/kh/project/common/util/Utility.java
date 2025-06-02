package edu.kh.project.common.util;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

// 프로그램 전체적으로 사용될 유용한 기능 모음
public class Utility {
	
	public static int seqNum = 1;  // 1 ~ 99999 반복
	
	private static final String CHARACTERS 
	= "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	private static final int CODE_LENGTH = 6;
	
	// 매개변수로 전달받은 원본명으로 파일의 변경명을 만들어 반환 메소드
	public static String fileRename(String originalFileName) {
		// 20250424150830_00001.jpg
		
		// SimpleDateFormat : 시간을 원하는 형태의 문자열로 간단히 변경
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		// java.util.Date() : 현재 시간을 저장한 자바 객체
		String date = sdf.format(new Date());
		
		String number = String.format("%05d", seqNum);
		
		seqNum++; // 1증가
		if(seqNum == 100000) seqNum = 1;
		
		
		// 확장자 구하기
		// "문자열".substring(인덱스)
		// - 문자열을 인덱스부터 끝까지 잘라낸 결과를 반환
		
		// "문자열".lastIndexOf(".")
		// - 문자열에서 마지막 "."의 인덱스를 반환
		
		String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
		
		// originalFileName == 짱구.jpg
		// ext == .jpg
		
		return date + "_" + number + ext; // 20250424150830_00001.jpg
		
		
	}
	
	// 랜덤 비밀번호 발급 메서드(관리자용)
	public static String generatePassword() {
		
		SecureRandom random = new SecureRandom();
		// SecureRandom : 난수를 생성하기 위한 클래스로, 
		//				  보안적으로 더 강력한 랜덤 값을 생성함
		// 일반적인 Random 보다 예측 가능성이 낮아, 민감한 데이터와 같은 곳에 적합함.
		
		StringBuilder randomCode = new StringBuilder(CODE_LENGTH);
		// 길이 6을 초기 용량으로 갖는 StringBuilder 객체 생성
		
		for(int i = 0; i < CODE_LENGTH; i++) {
			int index = random.nextInt(CHARACTERS.length());
			// random.nextInt(62) 는 0부터 61사이의 난수를 생성

			randomCode.append(CHARACTERS.charAt(index));
			// CHARACTERS 문자열의 index 위치에 있는 문자를 반환
			// ex) index 가 0 이면 'A', index 가 61 이면 '9'를 반환
			// 반환받은 값을 randomCode에 누적
		}
		
		return randomCode.toString();
	}
}
