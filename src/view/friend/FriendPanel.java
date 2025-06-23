package view.friend;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Base64;
import java.util.List;

import javax.swing.*;

import model.User;
import presenter.FriendPresenter;
import service.DAO.UserDatabase;

public class FriendPanel extends JPanel implements FriendView {
    private DefaultListModel<String> friendListModel = new DefaultListModel<>(); // 친구 목록 모델
    private JList<String> friendList = new JList<>(friendListModel);             // 친구 리스트 UI 컴포넌트
    private JLabel nicknameLabel;                                                // 내 닉네임 표시
    private JLabel introLabel;                                                   // 내 소개글
    private JLabel profileImg;                                                   // 내 프로필 이미지
    private JButton startChatButton;                                             // 채팅 시작 버튼
    private String userId;

    private FriendPresenter presenter;                                           // View-Logic 연결을 위한 프레젠터

    public FriendPanel(String userId) {
        this.userId = userId;
        this.presenter = new FriendPresenter(this, userId);  // Presenter 초기화

        // 전체 패널 설정
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 사용자 정보 조회
        User me = UserDatabase.shared().getUserById(userId);
        String nickname = (me != null && me.getNickname() != null) ? me.getNickname() : userId;
        String imageBase64 = (me != null) ? me.getImageBase64() : "";

        // 프로필 이미지 설정
        profileImg = new JLabel();
        profileImg.setPreferredSize(new Dimension(50, 50));
        setProfileImageFromBase64(imageBase64);  // base64 -> 이미지 변환

        // 닉네임 및 소개글 설정
        nicknameLabel = new JLabel(nickname);
        nicknameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        introLabel = new JLabel(" ");
        introLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        introLabel.setForeground(Color.DARK_GRAY);

        // 텍스트 정보 수직 정렬 패널
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(nicknameLabel);
        textPanel.add(introLabel);

        // 내 프로필 구성 패널
        JPanel myProfilePanel = new JPanel(new BorderLayout(10, 0));
        myProfilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        myProfilePanel.setBackground(Color.WHITE);
        myProfilePanel.add(profileImg, BorderLayout.WEST);
        myProfilePanel.add(textPanel, BorderLayout.CENTER);

        // 구분선 추가
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(200, 200, 200));

        // 프로필 영역 컨테이너
        JPanel profileContainer = new JPanel(new BorderLayout());
        profileContainer.setBackground(Color.WHITE);
        profileContainer.add(myProfilePanel, BorderLayout.CENTER);
        profileContainer.add(separator, BorderLayout.SOUTH);

        // 친구 리스트 설정
        friendList.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        friendList.setSelectionBackground(new Color(220, 240, 255));
        friendList.setBackground(Color.WHITE);

        // 친구 클릭 시 Presenter로 전달
        friendList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String selectedUser = friendList.getSelectedValue();
                if (selectedUser != null) {
                    presenter.onFriendSelected(selectedUser);
                    if (e.getClickCount() == 2) {
                        presenter.onFriendDoubleClicked(selectedUser);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(friendList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // 하단 채팅 시작 버튼 (기본 비가시화)
        startChatButton = new JButton();
        startChatButton.setVisible(false);
        startChatButton.addActionListener(e -> presenter.onStartChatButtonClicked());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        bottomPanel.add(startChatButton, BorderLayout.CENTER);

        // 전체 UI 배치
        add(profileContainer, BorderLayout.NORTH);  // 상단: 내 프로필
        add(scrollPane, BorderLayout.CENTER);       // 중앙: 친구 리스트
        add(bottomPanel, BorderLayout.SOUTH);       // 하단: 채팅 시작 버튼
    }

    // 친구 목록을 UI에 표시
    @Override
    public void showFriendList(List<String> users) {
        SwingUtilities.invokeLater(() -> {
            friendListModel.clear();
            for (String user : users)
                friendListModel.addElement(user);
        });
    }

    // 선택된 친구 또는 본인의 프로필 정보 표시
    @Override
    public void showUserProfile(String nickname, String intro, String imageBase64, boolean isMine) {
        String labelText = isMine ? "<html>" + nickname + " <span style='color:#66CCCC; font-size:8px;'>내 프로필</span></html>" : nickname;
        nicknameLabel.setText(labelText);
        introLabel.setText((intro != null && !intro.isEmpty()) ? intro : "(No introduction)");
        setProfileImageFromBase64(imageBase64);
    }

    // 내 닉네임 변경 시 갱신
    @Override
    public void updateMyInfo(String nickname) {
        nicknameLabel.setText(nickname);
        introLabel.setText(" ");
    }

    // 채팅 시작 버튼 활성화
    @Override
    public void enableChatButton(String friendId) {
        startChatButton.setText(friendId + "님과 채팅방 생성");
        startChatButton.setVisible(true);
    }

    // 채팅 시작 버튼 비활성화
    @Override
    public void disableChatButton() {
        startChatButton.setVisible(false);
    }

    // Base64 문자열을 이미지로 변환하여 프로필 사진 설정
    private void setProfileImageFromBase64(String imageBase64) {
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            try {
                byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
                ImageIcon icon = new ImageIcon(imageBytes);
                Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                profileImg.setIcon(new ImageIcon(img));
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // fallback 이미지 (기본 이미지 사용)
        try {
            ImageIcon fallback = new ImageIcon(getClass().getResource("/img/logo.png"));
            Image img = fallback.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            profileImg.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Presenter 객체 반환 (테스트/내부 호출용)
    public FriendPresenter getPresenter() {
        return presenter;
    }

    // 사용자 ID 반환
    public String getUserId() {
        return this.userId;
    }
}
