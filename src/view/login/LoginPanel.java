// 로그인 화면의 핵심 UI 패널
// 아이디/비밀번호 입력, 로그인 및 회원가입 버튼, presenter 연동 등 담당
package view.login;

import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.*;

import Controller.AppLauncher;
import presenter.LoginPresenter;
import view.signup.SignUpFrame;

public class LoginPanel extends JPanel implements LoginView {
    private JTextField idField;               // 사용자 ID 입력 필드
    private JPasswordField pwField;           // 비밀번호 입력 필드
    private JButton loginButton;              // 로그인 버튼
    private JButton joinButton;               // 회원가입 버튼
    private LoginPresenter presenter;         // View와 로직을 연결하는 Presenter

    // 로그인 패널 생성자
    public LoginPanel() {
        setBackground(Color.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // 수직 배치

        presenter = new LoginPresenter(this); // Presenter와 연결

        // 로고 이미지 추가
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/img/logo.png"));
        Image scaledImage = logoIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(scaledImage);
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(30));  // 위 여백
        add(logoLabel);
        add(Box.createVerticalStrut(30));  // 아래 여백

        // 입력 필드 구성
        idField = new JTextField(15);
        pwField = new JPasswordField(15);
        stylizeTextField(idField);
        stylizeTextField(pwField);

        add(centerPanel("아이디", idField));         // 아이디 필드
        add(centerPanel("비밀번호", pwField));       // 비밀번호 필드
        add(Box.createVerticalStrut(20));            // 여백

        // 버튼 구성
        loginButton = new JButton("로그인");
        joinButton = new JButton("회원가입");

        // 버튼 스타일 설정
        stylizeButton(loginButton, new Color(102, 204, 204), Color.WHITE);
        stylizeButton(joinButton, Color.LIGHT_GRAY, Color.BLACK);

        // 버튼 이벤트 핸들링
        loginButton.addActionListener(e -> presenter.onLoginClicked());
        joinButton.addActionListener(e -> new SignUpFrame()); // 회원가입 창 오픈

        // 버튼 패널 구성
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(joinButton);
        add(buttonPanel);
    }

    // 텍스트 필드 스타일 공통 설정
    private void stylizeTextField(JTextField field) {
        field.setMaximumSize(new Dimension(200, 30));
        field.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    // 버튼 스타일 공통 설정
    private void stylizeButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
    }

    // 입력 필드와 라벨을 수평으로 배치하는 패널 생성
    private JPanel centerPanel(String label, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);
        JLabel jlabel = new JLabel(label);
        jlabel.setPreferredSize(new Dimension(60, 30));
        panel.add(jlabel);
        panel.add(field);
        return panel;
    }

    // 사용자가 입력한 아이디 반환
    @Override
    public String getIdInput() {
        return idField.getText().trim();
    }

    // 사용자가 입력한 비밀번호 반환
    @Override
    public String getPasswordInput() {
        return new String(pwField.getPassword());
    }

    // 경고창 메시지 표시
    @Override
    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    // 로그인 성공 시 메인 화면으로 이동
    @Override
    public void moveToMainScreen(String id, Socket socket, PrintWriter out, BufferedReader in) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.dispose(); // 현재 로그인 창 닫기
        AppLauncher.launch(id, socket, out, in); // 메인 화면 실행
    }
}
