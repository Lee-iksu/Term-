package view.login;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public interface LoginView {

    // 사용자로부터 입력받은 ID 반환
    String getIdInput();

    // 사용자로부터 입력받은 비밀번호 반환
    String getPasswordInput();

    // 사용자에게 메시지를 다이얼로그 등으로 출력
    void showMessage(String msg);

    // 로그인 성공 시 메인 화면으로 전환
    void moveToMainScreen(String id, Socket socket, PrintWriter out, BufferedReader in);
}
