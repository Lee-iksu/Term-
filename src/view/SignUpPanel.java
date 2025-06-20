package view;

import presenter.SignUpPresenter;

import javax.swing.*;
import java.awt.*;

public class SignUpPanel extends JPanel implements SignUpView {
    private JTextField idField;
    private JPasswordField pwField;
    private JPasswordField pwCheckField;
    private JButton signUpButton;

    private SignUpPresenter presenter;

    public SignUpPanel() {
        presenter = new SignUpPresenter(this);

        setBackground(Color.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("회원가입");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(102, 204, 204));
        title.setAlignmentX(CENTER_ALIGNMENT);

        add(Box.createVerticalStrut(30));
        add(title);
        add(Box.createVerticalStrut(20));

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

        signUpButton = new JButton("가입");
        stylizeButton(signUpButton);
        signUpButton.addActionListener(e -> presenter.onSignUpClicked());

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(signUpButton);
        add(btnPanel);

        JLabel securityInfo = new JLabel("* 비밀번호는 암호화되어 저장됩니다");
        securityInfo.setFont(new Font("SansSerif", Font.ITALIC, 11));
        securityInfo.setForeground(Color.GRAY);
        securityInfo.setAlignmentX(CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(10));
        add(securityInfo);
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

    @Override
    public String getIdInput() {
        return idField.getText().trim();
    }

    @Override
    public String getPasswordInput() {
        return new String(pwField.getPassword());
    }

    @Override
    public String getPasswordCheckInput() {
        return new String(pwCheckField.getPassword());
    }

    @Override
    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    @Override
    public void closeWindow() {
        SwingUtilities.getWindowAncestor(this).dispose();
    }
}
