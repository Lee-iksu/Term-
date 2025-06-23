package view.signup;

import javax.swing.*;

// 회원가입 창을 띄우는 JFrame 구현체
public class SignUpFrame extends JFrame {
    
    public SignUpFrame() {
        setTitle("회원가입");                      // 창 제목 설정
        setSize(400, 200);                        // 초기 크기 설정 (pack() 전에 의미 없음)
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 닫기 동작: 이 창만 종료
        setLocationRelativeTo(null);              // 화면 중앙 정렬

        add(new SignUpPanel());                   // 실제 회원가입 UI 패널 삽입
        pack();                                   // 컴포넌트 크기에 맞게 자동 조정
        setSize(400, 500);                        // 원하는 고정 크기로 재설정

        setResizable(false);                      // 크기 조절 비활성화
        setVisible(true);                         // 화면에 표시
    }
}
