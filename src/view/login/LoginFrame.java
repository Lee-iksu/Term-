package view.login;

import javax.swing.*;

public class LoginFrame extends JFrame {

    // 로그인 프레임 생성자
    public LoginFrame() {
        // 윈도우 제목 설정
        setTitle("로그인 화면");

        // 프레임 기본 크기 설정
        setSize(400, 200);

        // 종료 시 프로그램 전체 종료
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 화면 중앙에 위치하도록 설정
        setLocationRelativeTo(null);

        // 로그인 패널을 프레임에 추가
        add(new LoginPanel());

        // 내부 구성 요소 크기에 맞게 자동 조정
        pack();

        // 높이 고정 (패널 높이 보장)
        setSize(400, 500);

        // 크기 조절 불가 설정
        setResizable(false);

        // 화면에 프레임 표시
        setVisible(true);
    }
}
