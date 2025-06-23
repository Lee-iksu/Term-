package view.main;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import javax.swing.*;

import Controller.ChatRoomController;
import model.Message;
import model.User;
import network.ClientCore;
import presenter.ChatPresenter;
import presenter.MainPresenter;
import presenter.ProfilePresenter;
import service.DAO.UserDatabase;
import view.chat.*;
import view.friend.FriendPanel;
import view.profile.ProfilePanel;
import view.schedule.SchedulePanel;

public class MainFrame extends JFrame implements MainView {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private ClientCore controller;

    private FriendPanel friendPanel;
    private ProfilePanel profilePanel;
    private ChatRoomListPanel chatRoomListPanel;
    private ChatPanel chatPanel;
    private SchedulePanel schedulePanel;
    private JLabel greetingLabel;

    private final String userId;
    private final User myUser;
    private final Socket socket;
    private final PrintWriter out;

    private ChatRoomController chatRoomController;
    private MainPresenter presenter;

    // 메인 프레임 생성자
    public MainFrame(String userId, Socket socket, PrintWriter out, BufferedReader in, ClientCore controller) {
        this.userId = userId;
        this.socket = socket;
        this.out = out;
        this.controller = controller;

        // 사용자 정보 복제
        User origin = UserDatabase.shared().getUserById(userId);
        this.myUser = new User(origin.getId(), origin.getPassword());
        this.myUser.setNickname(origin.getNickname());
        this.myUser.setIntro(origin.getIntro());

        this.presenter = new MainPresenter(this, userId, out, socket);

        // 기본 설정
        setTitle("OOPTalk - " + userId);
        setSize(420, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 상단 바 구성
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(102, 204, 204));
        topBar.setPreferredSize(new Dimension(getWidth(), 80));

        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setOpaque(false);

        // 인사말 라벨
        greetingLabel = new JLabel("좋은 하루 되세요, " + userId + "님");
        greetingLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        greetingLabel.setForeground(Color.WHITE);
        greetingLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 10));
        innerPanel.add(greetingLabel, BorderLayout.NORTH);

        // 탭 버튼 구성
        JPanel tabButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        tabButtons.setOpaque(false);

        JButton friendBtn = new JButton("친구목록");
        JButton chatBtn = new JButton("채팅방");
        JButton scheduleBtn = new JButton("일정");
        JButton profileBtn = new JButton("설정");
        JButton[] buttons = { friendBtn, chatBtn, scheduleBtn, profileBtn };

        // 버튼 스타일 적용
        styleTabButton(friendBtn, true);
        styleTabButton(chatBtn, false);
        styleTabButton(scheduleBtn, false);
        styleTabButton(profileBtn, false);

        tabButtons.add(friendBtn);
        tabButtons.add(chatBtn);
        tabButtons.add(scheduleBtn);
        tabButtons.add(profileBtn);

        innerPanel.add(tabButtons, BorderLayout.SOUTH);
        topBar.add(innerPanel, BorderLayout.CENTER);
        add(topBar, BorderLayout.NORTH);

        // 본문 패널(CardLayout 사용)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // 각 기능 패널 초기화
        friendPanel = new FriendPanel(userId);
        chatRoomListPanel = new ChatRoomListPanel();
        profilePanel = new ProfilePanel(null);
        ProfilePresenter profilePresenter = new ProfilePresenter(profilePanel, userId, socket, out, in, this);
        profilePanel.setPresenter(profilePresenter);
        int defaultRoomId = 0;
        schedulePanel = new SchedulePanel();
        schedulePanel.loadScheduleForRoom(defaultRoomId);

        // Presenter 간 의존성 연결
        friendPanel.getPresenter().setProfileController(profilePresenter);
        this.chatRoomController = new ChatRoomController(controller, userId, this);
        controller.setChatRoomController(this.chatRoomController);
        this.chatRoomController.setListPanel(chatRoomListPanel);
        friendPanel.getPresenter().setChatRoomController(chatRoomController);

        // 단체 채팅 생성 버튼 핸들러
        chatRoomListPanel.setGroupRoomCreationHandler(() -> {
            List<String> friends = friendPanel.getPresenter().getFriendList();
            new GroupChatSetupDialog(this, friends, (roomName, selectedIds) -> {
                chatRoomController.createGroupChatRoom(roomName, selectedIds);
            }).setVisible(true);
        });

        // 각 패널을 카드에 등록
        contentPanel.add(friendPanel, "FRIEND");
        contentPanel.add(chatRoomListPanel, "CHAT");
        contentPanel.add(schedulePanel, "SCHEDULE");
        contentPanel.add(profilePanel, "PROFILE");
        add(contentPanel, BorderLayout.CENTER);

        // 채팅방 선택 시 해당 방 열기 + 메시지 요청 전송
        chatRoomListPanel.setRoomSelectionHandler((roomName, roomId) -> {
            showChatRoom(roomName, roomId);
            Message getMsg = new Message();
            getMsg.setType("GET_MESSAGES");
            getMsg.setRoomId(roomId);
            getMsg.setId(userId);
            controller.send(getMsg);
        });

        // 상단 탭 버튼 클릭 이벤트
        friendBtn.addActionListener(e -> presenter.onFriendTabClicked());
        chatBtn.addActionListener(e -> presenter.onChatTabClicked());
        scheduleBtn.addActionListener(e -> presenter.onScheduleTabClicked());
        profileBtn.addActionListener(e -> presenter.onProfileTabClicked());

        // 초기 화면은 친구 목록
        cardLayout.show(contentPanel, "FRIEND");

        // 창 종료 시 로그아웃 처리
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                presenter.logout();
            }
        });

        setResizable(false);
        setVisible(true);
    }

    // 탭 버튼 스타일 설정
    private void styleTabButton(JButton btn, boolean isActive) {
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(51, 51, 51));
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(90, 30));

        if (isActive) {
            btn.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, new Color(200, 200, 200)));
        } else {
            btn.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(200, 200, 200)));
        }
    }

    // 탭 전환 구현 (MainView 인터페이스)
    @Override public void showFriendPanel() { cardLayout.show(contentPanel, "FRIEND"); }
    @Override public void showChatPanel() { cardLayout.show(contentPanel, "CHAT"); }
    @Override public void showSchedulePanel() { cardLayout.show(contentPanel, "SCHEDULE"); }
    @Override public void showProfilePanel() { cardLayout.show(contentPanel, "PROFILE"); }

    @Override
    public void updateGreeting(String nickname) {
        greetingLabel.setText("좋은 하루 되세요, " + nickname + "님");
    }

    // 채팅방 화면 전환 및 ChatPanel 갱신
    @Override
    public void showChatRoom(String roomName, int roomId) {
        if (chatPanel != null && chatPanel.getRoomId() == roomId) {
        	chatPanel.clearMessages();
            cardLayout.show(contentPanel, "CHAT_ROOM");
            return;
        }

        ChatPresenter presenter = new ChatPresenter(null, controller);
        ChatPanel newChatPanel = new ChatPanel(presenter, userId, roomId);
        presenter.setView(newChatPanel);
        this.chatPanel = newChatPanel;
        contentPanel.add(newChatPanel, "CHAT_ROOM");
        cardLayout.show(contentPanel, "CHAT_ROOM");
    }

    // Getter 메서드들
    public User getMyUser() { return myUser; }
    public FriendPanel getFriendPanel() { return friendPanel; }
    public ProfilePanel getProfilePanel() { return profilePanel; }
    public ChatPanel getChatPanel() { return chatPanel; }
    public SchedulePanel getSchedulePanel() { return schedulePanel; }
    public String getUserId() { return userId; }
    public ChatRoomListPanel getChatRoomListPanel() { return chatRoomListPanel; }
    public ChatRoomController getChatRoomController() { return this.chatRoomController; }
}
