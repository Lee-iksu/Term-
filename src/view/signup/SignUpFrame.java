package view.signup;

import javax.swing.*;

public class SignUpFrame extends JFrame {
    public SignUpFrame() {
        setTitle("회원가입");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        add(new SignUpPanel());
        pack();
        setSize(400, 500);
        
        setResizable(false);
        setVisible(true);


    }
}