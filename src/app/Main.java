package app; // 실행 시작

import javax.swing.SwingUtilities;

import view.login.LoginFrame; // 로그인 화면 // 첫 진입 UI

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 로그인창 생성 // 기본 프레임 띄움
            new LoginFrame(); 
        });
    }
}
