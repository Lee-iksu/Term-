// ProfilePanel.java (MainView)
package view;

import Controller.ProfileController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Base64;

public class ProfilePanel extends JPanel {
    private JTextField nicknameField;
    private JTextField introField;
    private JLabel photoLabel;
    private ImageIcon profileImage;
    private String imageBase64 = "";

    private final ProfileController controller;

    public ProfilePanel(ProfileController controller) {
        this.controller = controller;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("프로필 설정", JLabel.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        title.setAlignmentX(CENTER_ALIGNMENT);
        add(title);
        add(Box.createRigidArea(new Dimension(0, 20)));

        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(100, 100));
        photoLabel.setOpaque(true);
        photoLabel.setBackground(Color.LIGHT_GRAY);
        photoLabel.setHorizontalAlignment(JLabel.CENTER);
        photoLabel.setVerticalAlignment(JLabel.CENTER);
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        photoLabel.setToolTipText("클릭하여 프로필 사진 업로드");
        
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/img/logo.png"));
        Image logoImg = logoIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        photoLabel.setIcon(new ImageIcon(logoImg));

        photoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.selectProfileImage(ProfilePanel.this);
            }
        });

        photoLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(photoLabel);
        add(Box.createRigidArea(new Dimension(0, 10)));

        JButton uploadButton = new JButton("사진 업로드");
        uploadButton.setBackground(new Color(102, 204, 204));
        uploadButton.setForeground(Color.WHITE);
        uploadButton.setFocusPainted(false);
        uploadButton.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        uploadButton.setPreferredSize(new Dimension(100, 30));
        uploadButton.setAlignmentX(CENTER_ALIGNMENT);
        uploadButton.addActionListener(e -> controller.selectProfileImage(ProfilePanel.this));
        add(uploadButton);

        add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel nicknameLabel = new JLabel("닉네임:");
        nicknameLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(nicknameLabel);
        nicknameField = new JTextField();
        nicknameField.setMaximumSize(new Dimension(200, 25));
        nicknameField.setAlignmentX(CENTER_ALIGNMENT);
        add(nicknameField);

        add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel introLabel = new JLabel("소개:");
        introLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(introLabel);
        introField = new JTextField();
        introField.setMaximumSize(new Dimension(400, 25));
        introField.setAlignmentX(CENTER_ALIGNMENT);
        add(introField);

        add(Box.createRigidArea(new Dimension(0, 20)));

        JButton saveButton = new JButton("저장");
        saveButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        saveButton.setBackground(new Color(102, 204, 204));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setPreferredSize(new Dimension(100, 40));
        saveButton.setAlignmentX(CENTER_ALIGNMENT);
        saveButton.addActionListener(e -> controller.handleSaveProfile(nicknameField.getText().trim(), introField.getText().trim(), imageBase64));
        add(saveButton);

        controller.requestMyProfile();
    }

    public void setProfileImage(ImageIcon imageIcon, String imageBase64) {
        this.imageBase64 = imageBase64;
        this.profileImage = imageIcon;
        photoLabel.setIcon(profileImage);
    }

    public void displayUserProfile(String nickname, String intro, String imageBase64) {
        nicknameField.setText(nickname);
        introField.setText(intro);

        if (imageBase64 == null || imageBase64.isEmpty()) {
            ImageIcon fallback = new ImageIcon(getClass().getResource("/img/logo.png"));
            Image logoImg = fallback.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            photoLabel.setIcon(new ImageIcon(logoImg));
        } else {
            controller.decodeAndDisplayImage(imageBase64, photoLabel);
        }
    }

}
