package view;

import javax.swing.*;
import java.awt.*;

import model.User;
import service.UserDatabase;

public class SignUpPanel extends JPanel {
    private JTextField idField;
    private JPasswordField pwField;
    private JPasswordField pwCheckField;
    private JButton signUpButton;

    public SignUpPanel() {
        setBackground(Color.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // 타이틀
        JLabel title = new JLabel("회원가입");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(102, 204, 204));
        title.setAlignmentX(CENTER_ALIGNMENT);

        add(Box.createVerticalStrut(30));
        add(title);
        add(Box.createVerticalStrut(20));

        // 입력창
        idField = new JTextField(15);
        pwField = new JPasswordField(15);
        pwCheckField = new JPasswordField(15);

        stylizeField(idField);
        stylizeField(pwField);
        stylizeField(pwCheckField);

        add(centerPanel("아이디", idField));
        add(centerPanel("비밀번호", pwField));
        add(centerPanel("비밀번호 확인", pwCheckField));
        add(Box.createVerticalStrut(20));

        // 버튼
        signUpButton = new JButton("가입");
        stylizeButton(signUpButton);
        signUpButton.addActionListener(e -> handleSignUp());

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(signUpButton);
        add(btnPanel);
        
        // 비밀번호 보안 안내 메시지
        JLabel securityInfo = new JLabel("* 비밀번호는 암호화되어 안전하게 저장됩니다");
        securityInfo.setFont(new Font("SansSerif", Font.ITALIC, 11));
        securityInfo.setForeground(Color.GRAY);
        securityInfo.setAlignmentX(CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(10));
        add(securityInfo);
    }

    private void handleSignUp() {
        String id = idField.getText().trim();
        String pw = new String(pwField.getPassword());
        String pwCheck = new String(pwCheckField.getPassword());

        if (id.isEmpty() || pw.isEmpty() || pwCheck.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 항목을 입력해주세요.");
            return;
        }

        if (!pw.equals(pwCheck)) {
            JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.");
            return;
        }

        UserDatabase db = UserDatabase.shared();
        if (db.isDuplicateId(id)) {
            JOptionPane.showMessageDialog(this, "이미 존재하는 ID입니다.");
            return;
        }

        // 비밀번호는 User 객체 생성 시 암호화됨
        db.addUser(new User(id, pw));
        JOptionPane.showMessageDialog(this, "회원가입 성공! 비밀번호가 암호화되어 저장되었습니다.");
        SwingUtilities.getWindowAncestor(this).dispose(); // 창 닫기
    }

    private JPanel centerPanel(String label, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);
        JLabel jlabel = new JLabel(label);
        jlabel.setPreferredSize(new Dimension(90, 30));
        panel.add(jlabel);
        panel.add(field);
        return panel;
    }

    private void stylizeField(JTextField field) {
        field.setMaximumSize(new Dimension(200, 30));
        field.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    private void stylizeButton(JButton btn) {
        btn.setBackground(new Color(102, 204, 204));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
    }
}