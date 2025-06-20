package presenter;

import service.*;
import view.LoginView;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginPresenter {
    private LoginView view;
    private LoginService loginService = new LoginService();

    public LoginPresenter(LoginView view) {
        this.view = view;
    }

    public void onLoginClicked() {
        String id = view.getIdInput();
        String pw = view.getPasswordInput();

        if (id.isEmpty() || pw.isEmpty()) {
            view.showMessage("아이디와 비밀번호를 모두 입력하세요.");
            return;
        }

        if (!loginService.authenticate(id, pw)) {
            view.showMessage("로그인 실패: 아이디 또는 비밀번호가 틀렸습니다.");
            return;
        }

        try {
            Socket socket = ConnectionManager.connect();
            BufferedReader in = ConnectionManager.getReader(socket);
            PrintWriter out = ConnectionManager.getWriter(socket);

            view.moveToMainScreen(id, socket, out, in);
        } catch (Exception ex) {
            view.showMessage("서버 연결 실패: " + ex.getMessage());
        }
    }
}
