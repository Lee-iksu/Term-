package view.profile;

import presenter.ProfilePresenter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ProfilePanel extends JPanel implements ProfileView {
    private JTextField nicknameField;          // 닉네임 입력 필드
    private JTextField introField;             // 소개 입력 필드
    private JLabel photoLabel;                 // 프로필 이미지 라벨
    private ImageIcon profileImage;            // 현재 표시 중인 이미지 객체
    private String imageBase64 = "";           // 이미지의 Base64 문자열

    private ProfilePresenter presenter;        // View-Logic 연동 객체

    // 생성자: presenter 주입
    public ProfilePanel(ProfilePresenter presenter) {
        this.presenter = presenter;
        initUI(); // UI 구성 메서드 호출
    }

    // UI 구성 메서드
    private void initUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // 제목 라벨
        JLabel title = new JLabel("프로필 설정", JLabel.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        title.setAlignmentX(CENTER_ALIGNMENT);
        add(title);
        add(Box.createRigidArea(new Dimension(0, 20)));

        // 프로필 이미지 라벨 초기화
        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(100, 100));
        photoLabel.setOpaque(true);
        photoLabel.setBackground(Color.LIGHT_GRAY);
        photoLabel.setHorizontalAlignment(JLabel.CENTER);
        photoLabel.setVerticalAlignment(JLabel.CENTER);
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        photoLabel.setToolTipText("클릭하여 프로필 사진 업로드");

        setDefaultProfileImage(); // 기본 이미지 설정

        // 이미지 클릭 시 이미지 선택 창 열기
        photoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                presenter.selectProfileImage();
            }
        });

        photoLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(photoLabel);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // 사진 업로드 버튼
        JButton uploadButton = createButton("사진 업로드", e -> presenter.selectProfileImage());
        add(uploadButton);
        add(Box.createRigidArea(new Dimension(0, 20)));

        // 텍스트 필드 (닉네임, 소개)
        nicknameField = createLabeledTextField("닉네임:");
        introField = createLabeledTextField("소개:");
        add(Box.createRigidArea(new Dimension(0, 20)));

        // 저장 버튼 구성
        JButton saveButton = new JButton("저장");
        saveButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        saveButton.setBackground(new Color(102, 204, 204));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setPreferredSize(new Dimension(100, 40));
        saveButton.setAlignmentX(CENTER_ALIGNMENT);

        // 저장 버튼 클릭 → presenter에게 저장 요청
        saveButton.addActionListener(e -> presenter.handleSaveProfile(
            nicknameField.getText().trim(),
            introField.getText().trim(),
            imageBase64
        ));

        add(saveButton);
    }

    // 기본 프로필 이미지 설정
    private void setDefaultProfileImage() {
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/img/logo.png"));
        Image logoImg = logoIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        photoLabel.setIcon(new ImageIcon(logoImg));
    }

    // 공통 버튼 생성기
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

    // 공통 텍스트필드 + 라벨 묶음 생성기
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

    // 프로필 이미지 설정 (View 인터페이스 구현)
    @Override
    public void setProfileImage(ImageIcon imageIcon, String imageBase64) {
        this.imageBase64 = imageBase64;
        this.profileImage = imageIcon;
        photoLabel.setIcon(profileImage);
    }

    // 사용자 정보 로딩 시 필드에 반영
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

    // presenter 주입 (초기화 이후에 호출 가능)
    public void setPresenter(ProfilePresenter presenter) {
        this.presenter = presenter;
    }
}
