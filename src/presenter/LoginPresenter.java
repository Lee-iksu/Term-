package presenter;

import service.*;
import view.login.LoginView;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginPresenter {
    // 로그인 화면과 상호작용하는 뷰 객체
    private LoginView view;

    // 로그인 인증을 처리하는 서비스 객체
    private LoginService loginService = new LoginService();

    // 생성자: 뷰 객체를 받아 Presenter 초기화
    public LoginPresenter(LoginView view) {
        this.view = view;
    }

    // 로그인 버튼 클릭 시 호출되는 메서드
    public void onLoginClicked() {
        String id = view.getIdInput();           // 사용자 입력 아이디
        String pw = view.getPasswordInput();     // 사용자 입력 비밀번호

        // 필수 입력값 확인
        if (id.isEmpty() || pw.isEmpty()) {
            view.showMessage("아이디와 비밀번호를 모두 입력하세요."); // 누락된 필드 경고
            return;
        }

        // 로그인 인증 시도
        if (!loginService.authenticate(id, pw)) {
            view.showMessage("로그인 실패: 아이디 또는 비밀번호가 틀렸습니다."); // 인증 실패 안내
            return;
        }

        try {
            // 서버와의 소켓 연결 시도
            Socket socket = ConnectionManager.connect();                        // 서버 연결
            BufferedReader in = ConnectionManager.getReader(socket);           // 입력 스트림 확보
            PrintWriter out = ConnectionManager.getWriter(socket);             // 출력 스트림 확보

            // 로그인 성공 시 메인 화면으로 전환
            view.moveToMainScreen(id, socket, out, in);
        } catch (Exception ex) {
            // 서버 연결 실패 시 사용자에게 메시지 표시
            view.showMessage("서버 연결 실패: " + ex.getMessage());
        }
    }
}
