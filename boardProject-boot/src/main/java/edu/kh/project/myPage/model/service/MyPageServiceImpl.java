package edu.kh.project.myPage.model.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.common.util.Utility;
import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.dto.UploadFile;
import edu.kh.project.myPage.model.mapper.MyPageMapper;

@Service
@Transactional(rollbackFor = Exception.class)
@PropertySource("classpath:/config.properties")
public class MyPageServiceImpl implements MyPageService{
	
	@Autowired
	private MyPageMapper mapper;
	
	// Bcrypt 암호화 객체 의존성 주입(SecurityConfig 참고)
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	@Value("${my.profile.web-path}")
	private String profileWebPath; //   /myPage/profile/
	
	@Value("${my.profile.folder-path}")
	private String profileFolderPath; //   C:/uploadFiles/profile/
	

	@Override
 	public int updateInfo(Member inputMember, String[] memberAddress) {
		
		// 입력된 주소가 있을 경우
		if(!inputMember.getMemberAddress().equals(",,")) {
			
			String address = String.join("^^^", memberAddress);
			inputMember.setMemberAddress(address);
			
		} else {
		// 없을 경우
			inputMember.setMemberAddress(null);
			
		}
		
		
		return mapper.updateInfo(inputMember);
	}
	
	// 비밀번호 변경 서비스
	@Override
	public int changePw(Map<String, String> paramMap, int memberNo) {
		
		// 1. 현재 비밀번호가 일치하는지 확인하기
		// - 현재 로그인한 회원의 암호화된 비밀번호를 DB에서 조회
		String originPw = mapper.selectPw(memberNo);
		
		// 입력받은 현재 비밀번호와(평문)
		// DB에서 조회한 비밀번호(암호화)를 비교
		// -> bcrypt.matches(평문, 암호화비번) 사용
		
		// 다를 경우
		if( !bcrypt.matches(paramMap.get("currentPw"), originPw) ) {
			return 0;
		}
		
		// 2. 같을 경우
		
		// 새 비밀번호를 암호화 (bcrypt.encode(평문))
		String encPw = bcrypt.encode(paramMap.get("newPw"));
		
		// DB에 업데이트
		// SQL 전달 해야하는 데이터 2개 (암호화한 새 비번 encPw , 회원번호 memberNo)
		// -> mapper에 전달할수 있는 전달인자는 단 1개!
		// -> 묶어서 전달 (paramMap 재활용)
		
		paramMap.put("encPw", encPw);
		paramMap.put("memberNo", memberNo + "");
		
		return mapper.changePw(paramMap);
	}
	
	// 회원 탈퇴 서비스
	@Override
	public int secession(String memberPw, int memberNo) {
		
		// 현재 로그인한 회원의 암호화된 비밀번호 DB 조회
		String originPw = mapper.selectPw(memberNo);
		
		// 다를 경우
		if( !bcrypt.matches(memberPw, originPw) ) {
			return 0;
		}
		
		// 같은 경우
		return mapper.secession(memberNo);
	}
	
	// 파일 업로드 테스트 1
	@Override
	public String fileUpload1(MultipartFile uploadFile) throws Exception {
		
		// MultipartFile 이 제공하는 메소드
		// - getSize() : 파일 크기
		// - isEmpty() : 업로드한 파일이 없을 경우 true / 있다면 false
		// - getOriginalFileName() : 원본 파일명
		// - transferTo(경로) : 
		//	메모리 또는 임시 저장 경로에 업로드된 파일을
		//  원하는 경로에 실제로 전송(서버 어떤 경로 폴더에 저장할지 지정)
		
		// 업로드한 파일이 없을 경우
		if( uploadFile.isEmpty() ) {
			return null;
		}
		
		// 업로드한 파일이 있을 경우
		// C:/uploadFiles/test/파일명 으로 서버에 저장
		uploadFile.transferTo(new File("C:/uploadFiles/test/" + uploadFile.getOriginalFilename()));
		
		// 웹에서 해당 파일에 접근할 수 있는 경로를 반환
		// 서버 : C:/uploadFiles/test/A.jpg
		// 웹 접근 주소 : /myPage/file/A.jpg
		
		return "/myPage/file/" + uploadFile.getOriginalFilename();
	}
	
	// 파일 업로드 테스트 2 (+DB)
	@Override
	public int fileUpload2(MultipartFile uploadFile, int memberNo) throws Exception {
		
		// 업로드된 파일이 없을때
		if(uploadFile.isEmpty()) {
			return 0;
		}
		
		/* DB에 파일 저장이 가능은 하지만
		 * DB 부하를 줄이기 위해서 
		 * 
		 * 1) DB 에는 서버에 저장할 파일 경로를 저장
		 * 
		 * 2) DB 삽입 / 수정 성공 후 서버에 파일을 저장
		 * 
		 * 3) 만약에 파일 저장 실패 시 
		 * 	-> 예외 발생
		 *  -> @Transactional을 이용해서 rollback 수행
		 * 
		 * */
		
		// 1. 서버에 저장할 파일 경로 만들기
		
		// 파일이 저장될 서버 폴더 경로
		String folderPath = "C:/uploadFiles/test/";
		
		// 클라이언트가 파일이 저장된 폴더에 접근할 수 있는 주소(정적리소스 요청 주소)
		String webPath = "/myPage/file/";
		
		
		// 2. DB에 전달할 데이터를 DTO로 묶기
		// webPath, memberNo, 원본 파일명, 변경된 파일명
		String fileRename = Utility.fileRename(uploadFile.getOriginalFilename());
		
		// Builder 패턴을 이용해서 UploadFile 객체 생성
		// 장점 1) 반복되는 참조변수명, set 구문 생략
		// 장점 2) method chaining을 이용하여 한줄로 작성 가능
		// 장점 3) 매개변수 생성자 불필요
		UploadFile uf = UploadFile.builder()
						.memberNo(memberNo)
						.filePath(webPath)
						.fileOriginalName(uploadFile.getOriginalFilename())
						.fileRename(fileRename)
						.build();
		
		// 3. DTO 객체를 DB에 전달하기 (INSERT 하기)
		int result = mapper.insertUploadFile(uf);
		
		// 4. 삽입 성공 시 파일을 지정된 서버 폴더에 저장
		if(result == 0) return 0;
		
		// folderPath경로(C:/uploadFiles/test/변경된파일명) 으로
		// 파일을 서버 컴퓨터에 저장!
		uploadFile.transferTo(new File(folderPath+fileRename));
			// C:/uploadFiles/test/20250424150830_00001.jpg
		
		
		
		return result;
	}
	
	
	@Override
	public List<UploadFile> fileList(int memberNo) {
		return mapper.fileList(memberNo);
	}
	
	// 여러 파일 업로드 서비스
	@Override
	public int fileUpload3(List<MultipartFile> aaaList, 
							List<MultipartFile> bbbList, 
							int memberNo) throws Exception {
		
		// 1. aaaList 처리
		int result1 = 0; // 결과(INSERT된 행의 갯수)를 저장할 변수
		
		// 업로드된 파일이 없을 경우를 제외하고 업로드
		for(MultipartFile file : aaaList) {
			
			if(file.isEmpty()) { // 파일이 없으면 다음 파일
				continue;
			}
			
			// DB에 저장 + 서버 실제로 저장
			// fileUpload2() 메서드를 호출(재활용)
			result1 += fileUpload2(file, memberNo);
		}
		
		// 2. bbbList 처리
		int result2 = 0; // 결과(INSERT된 행의 갯수)를 저장할 변수
		
		// 업로드된 파일이 없을 경우를 제외하고 업로드
		for(MultipartFile file : bbbList) {
			
			if(file.isEmpty()) { // 파일이 없으면 다음 파일
				continue;
			}
			
			// DB에 저장 + 서버 실제로 저장
			// fileUpload2() 메서드를 호출(재활용)
			result2 += fileUpload2(file, memberNo);
		}
		
		return result1 + result2;
	}
	
	// 프로필 이미지 변경 서비스
	@Override
	public int profile(MultipartFile profileImg, Member loginMember) throws Exception {
		
		// 프로필 이미지 경로 
		String updatePath = null;
		
		// 변경명 저장
		String rename = null;
		
		// 업로드한 이미지가 있을 경우
		// - 있을 경우 : 경로 조합 (클라이언트 접근 경로 + 리네임파일명)
		if( !profileImg.isEmpty() ) {
			// 1. 파일명 변경
			rename = Utility.fileRename(profileImg.getOriginalFilename());
			
			// 2. /myPage/profile/변경된파일명
			updatePath = profileWebPath + rename;
		}
		
		// 수정된 프로필 이미지 경로 + 회원 번호를 저장할 DTO 객체
		Member member = Member.builder()
						.memberNo(loginMember.getMemberNo())
						.profileImg(updatePath)
						.build();
		
		int result = mapper.profile(member);
		
		if(result > 0) { 
			
			// 프로필 이미지를 없애는 update 를 한 경우를 제외
			// -> 업로드한 이미지가 있을 경우
			if( !profileImg.isEmpty() ) {
				// 파일을 서버에 저장
				profileImg.transferTo(new File(profileFolderPath + rename));
							// C:/uploadFiles/profile/변경한이름
			}
			
			// 세션에 저장된 loginMember의 프로필 이미지 경로를
			// DB와 동기화
			loginMember.setProfileImg(updatePath);
			
			
		}
		
		return result;
	}
	
	
	
	
	
	
}
