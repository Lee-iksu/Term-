package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import Controller.LoginController;

public class LoginPanel extends JPanel {
    private JTextField idField;
    private JPasswordField pwField;
    private JButton loginButton;
    private JButton joinButton;

    public LoginPanel() {
        setBackground(Color.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //앱 로고
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/img/logo.png"));
        Image scaledImage = logoIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(scaledImage);

        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(30));
        add(logoLabel);
        
        //여백
        add(Box.createVerticalStrut(30));

        // 입력 필드
        idField = new JTextField(15);
        pwField = new JPasswordField(15);

        stylizeTextField(idField);
        stylizeTextField(pwField);

        add(centerPanel("아이디", idField));
        add(centerPanel("비밀번호", pwField));
        add(Box.createVerticalStrut(20));

        // 버튼
        loginButton = new JButton("로그인");
        joinButton = new JButton("회원가입");

        stylizeButton(loginButton, new Color(102, 204, 204), Color.WHITE);
        stylizeButton(joinButton, Color.LIGHT_GRAY, Color.BLACK);

        loginButton.addActionListener(new LoginController(idField, pwField));
        joinButton.addActionListener(e -> new SignUpFrame());

        // 버튼 정렬
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(joinButton);
        add(buttonPanel);
    }

    private void stylizeTextField(JTextField field) {
        field.setMaximumSize(new Dimension(200, 30));
        field.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    private void stylizeButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
    }

    private JPanel centerPanel(String label, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);
        JLabel jlabel = new JLabel(label);
        jlabel.setPreferredSize(new Dimension(60, 30));
        panel.add(jlabel);
        panel.add(field);
        return panel;
    }
}