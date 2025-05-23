package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.google.gson.Gson;

import Controller.ChatRoomController;
import Controller.MultiChatController;
import Controller.ProfileController;
import model.Message;
import model.User;
import service.UserDatabase;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;

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

    public MainFrame(String userId, Socket socket, PrintWriter out, BufferedReader in) {
        this.userId = userId;
        this.socket = socket;
        this.out = out;

        User origin = UserDatabase.shared().getUserById(userId);
        this.myUser = new User(origin.getId(), origin.getPassword());
        this.myUser.setNickname(origin.getNickname());
        this.myUser.setIntro(origin.getIntro());

        setTitle("OOPTalk - " + userId);
        setSize(420, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(102, 204, 204));
        topBar.setPreferredSize(new Dimension(getWidth(), 80));

        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setOpaque(false);

        greetingLabel = new JLabel("좋은 하루 되세요, " + userId + "님");
        greetingLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        greetingLabel.setForeground(Color.WHITE);
        greetingLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 10));
        innerPanel.add(greetingLabel, BorderLayout.NORTH);

        JPanel tabButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        tabButtons.setOpaque(false);

        JButton friendBtn = new JButton("친구목록");
        JButton chatBtn = new JButton("채팅방");
        JButton scheduleBtn = new JButton("일정");
        JButton profileBtn = new JButton("설정");

        JButton[] buttons = { friendBtn, chatBtn, scheduleBtn, profileBtn };

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

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        friendPanel = new FriendPanel(userId);
        chatRoomListPanel = new ChatRoomListPanel();
        ProfileController profileController = new ProfileController(userId, socket, out, in, this);
        profilePanel = new ProfilePanel(profileController);
        schedulePanel = new SchedulePanel(null, userId);

        friendPanel.setProfilePanel(profilePanel);
        friendPanel.setProfileController(profileController);

        // ChatRoomController 설정
        MultiChatController controller = new MultiChatController(null, null, this); // 실제 인스턴스는 외부에서 set
        controller.setIO(socket, out, in);
        
        chatRoomController = new ChatRoomController(controller, userId, this);
        controller.setChatRoomController(chatRoomController);
        friendPanel.setChatRoomController(chatRoomController);

        contentPanel.add(friendPanel, "FRIEND");
        contentPanel.add(chatRoomListPanel, "CHAT");
        contentPanel.add(schedulePanel, "SCHEDULE");
        contentPanel.add(profilePanel, "PROFILE");

        add(contentPanel, BorderLayout.CENTER);

        chatRoomListPanel.setRoomSelectionHandler((roomName, roomId) -> {
            this.chatPanel = new ChatPanel(null, userId, roomId);
            contentPanel.add(chatPanel, "CHAT_ROOM");
            cardLayout.show(contentPanel, "CHAT_ROOM");
        });

        friendBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "FRIEND");
            styleTabButton(friendBtn, true);
            styleTabButton(chatBtn, false);
            styleTabButton(scheduleBtn, false);
            styleTabButton(profileBtn, false);
        });

        chatBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "CHAT");
            styleTabButton(friendBtn, false);
            styleTabButton(chatBtn, true);
            styleTabButton(scheduleBtn, false);
            styleTabButton(profileBtn, false);
        });

        scheduleBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "SCHEDULE");
            styleTabButton(friendBtn, false);
            styleTabButton(chatBtn, false);
            styleTabButton(scheduleBtn, true);
            styleTabButton(profileBtn, false);
        });

        profileBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "PROFILE");
            styleTabButton(friendBtn, false);
            styleTabButton(chatBtn, false);
            styleTabButton(scheduleBtn, false);
            styleTabButton(profileBtn, true);
        });

        cardLayout.show(contentPanel, "FRIEND");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    Gson gson = new Gson();
                    Message logoutMsg = new Message(userId, "", "logout", "all", "", null, 0);
                    out.println(gson.toJson(logoutMsg));
                    out.flush();
                    Thread.sleep(300);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    System.out.println("로그아웃 메시지 전송 후 종료");
                }
            }
        });

        setResizable(false);
        setVisible(true);
    }

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

    public User getMyUser() {
        return myUser;
    }

    public void updateGreeting() {
        String nickname = (myUser != null && myUser.getNickname() != null) ? myUser.getNickname() : userId;
        greetingLabel.setText("좋은 하루 되세요, " + nickname + "님");
    }

    public FriendPanel getFriendPanel() {
        return friendPanel;
    }

    public ProfilePanel getProfilePanel() {
        return profilePanel;
    }

    public ChatPanel getChatPanel() {
        return chatPanel;
    }

    public SchedulePanel getSchedulePanel() {
        return schedulePanel;
    }

    public String getUserId() {
        return userId;
    }

    public ChatRoomListPanel getChatRoomListPanel() {
        return chatRoomListPanel;
    }

    public void showChatRoom(String roomName, int roomId) {
        this.chatPanel = new ChatPanel(null, userId, roomId);
        contentPanel.add(chatPanel, "CHAT_ROOM");
        cardLayout.show(contentPanel, "CHAT_ROOM");
    }
}