package view.login;

import javax.swing.*;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        setTitle("로그인 화면");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 가운데 정렬

        add(new LoginPanel());
        pack();
        setSize(400, 500);

        setResizable(false);
        setVisible(true);


    }
}