package service;

/**
 * 로그인 인증 서비스 클래스
 * 
 * - 사용자 입력 ID/PW를 DAO에 전달
 * - 비밀번호 유효성 검사만 담당
 * 
 * 내부에서 UserDatabase (UserDAO) 호출
 * - UI에서 직접 DAO 호출하지 않도록 중간 계층 역할
 */


public class LoginService {
    public boolean authenticate(String id, String pw) {
        return service.DAO.UserDatabase.shared().isValidUser(id, pw);
    }
}
