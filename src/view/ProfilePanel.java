package view;

import presenter.ProfilePresenter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ProfilePanel extends JPanel implements ProfileView {
    private JTextField nicknameField;
    private JTextField introField;
    private JLabel photoLabel;
    private ImageIcon profileImage;
    private String imageBase64 = "";

    private ProfilePresenter presenter;

    public ProfilePanel(ProfilePresenter presenter) {
        this.presenter = presenter;
        initUI();
    }

    private void initUI() {
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

        setDefaultProfileImage();

        photoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                presenter.selectProfileImage();
            }
        });

        photoLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(photoLabel);
        add(Box.createRigidArea(new Dimension(0, 10)));

        JButton uploadButton = createButton("사진 업로드", e -> presenter.selectProfileImage());
        add(uploadButton);
        add(Box.createRigidArea(new Dimension(0, 20)));

        nicknameField = createLabeledTextField("닉네임:");
        introField = createLabeledTextField("소개:");

        add(Box.createRigidArea(new Dimension(0, 20)));

        JButton saveButton = new JButton("저장");
        saveButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        saveButton.setBackground(new Color(102, 204, 204));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setPreferredSize(new Dimension(100, 40));
        saveButton.setAlignmentX(CENTER_ALIGNMENT);
        saveButton.addActionListener(e -> presenter.handleSaveProfile(
            nicknameField.getText().trim(),
            introField.getText().trim(),
            imageBase64
        ));
        add(saveButton);
    }

    private void setDefaultProfileImage() {
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/img/logo.png"));
        Image logoImg = logoIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        photoLabel.setIcon(new ImageIcon(logoImg));
    }

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(new Color(102, 204, 204));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        button.setPreferredSize(new Dimension(100, 30));
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.addActionListener(listener);
        return button;
    }

    private JTextField createLabeledTextField(String labelText) {
        JLabel label = new JLabel(labelText);
        label.setAlignmentX(CENTER_ALIGNMENT);
        add(label);

        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(400, 25));
        field.setAlignmentX(CENTER_ALIGNMENT);
        add(field);
        add(Box.createRigidArea(new Dimension(0, 10)));
        return field;
    }

    @Override
    public void setProfileImage(ImageIcon imageIcon, String imageBase64) {
        this.imageBase64 = imageBase64;
        this.profileImage = imageIcon;
        photoLabel.setIcon(profileImage);
    }

    @Override
    public void displayUserProfile(String nickname, String intro, String imageBase64) {
        nicknameField.setText(nickname);
        introField.setText(intro);

        if (imageBase64 == null || imageBase64.isEmpty()) {
            setDefaultProfileImage();
        } else {
            presenter.decodeAndDisplayImage(imageBase64, photoLabel);
        }
    }
    
    public void setPresenter(ProfilePresenter presenter) {
        this.presenter = presenter;
    }

}
