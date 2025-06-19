package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import Controller.ProfileController;
import model.User;
import service.UserDatabase;

public class FriendPanel extends JPanel {
    private DefaultListModel<String> friendListModel = new DefaultListModel<>();
    private JList<String> friendList = new JList<>(friendListModel);
    private ProfilePanel profilePanel;
    private ProfileController profileController;
    private Controller.ChatRoomController chatRoomController;

    private String userId;
    private JLabel nicknameLabel;
    private JLabel introLabel;
    private JLabel profileImg;

    private JPanel bottomPanel;
    private JButton startChatButton;
    private String selectedFriendId = null;

    public FriendPanel(String userId) {
        this.userId = userId;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        User me = UserDatabase.shared().getUserById(userId);
        String nickname = (me != null && me.getNickname() != null) ? me.getNickname() : userId;
        String imageBase64 = (me != null) ? me.getImageBase64() : "";

        JPanel myProfilePanel = new JPanel(new BorderLayout(10, 0));
        myProfilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        myProfilePanel.setBackground(Color.WHITE);

        profileImg = new JLabel();
        profileImg.setPreferredSize(new Dimension(50, 50));
        setProfileImageFromBase64(imageBase64);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        nicknameLabel = new JLabel(nickname);
        nicknameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        nicknameLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        introLabel = new JLabel(" ");
        introLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        introLabel.setForeground(Color.DARK_GRAY);
        introLabel.setHorizontalAlignment(SwingConstants.LEFT);
        introLabel.setVerticalAlignment(SwingConstants.TOP);
        introLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        textPanel.add(nicknameLabel);
        textPanel.add(introLabel);

        myProfilePanel.add(profileImg, BorderLayout.WEST);
        myProfilePanel.add(textPanel, BorderLayout.CENTER);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(200, 200, 200));
        separator.setPreferredSize(new Dimension(1, 1));

        JPanel profileContainer = new JPanel(new BorderLayout());
        profileContainer.setBackground(Color.WHITE);
        profileContainer.add(myProfilePanel, BorderLayout.CENTER);
        profileContainer.add(separator, BorderLayout.SOUTH);

        friendList.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        friendList.setSelectionBackground(new Color(220, 240, 255));
        friendList.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(friendList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        friendList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String selectedUser = friendList.getSelectedValue();
                if (selectedUser != null && !selectedUser.equals(userId)) {
                    selectedFriendId = selectedUser;
                    startChatButton.setText(selectedUser + "님과 채팅방 생성");
                    startChatButton.setVisible(true);
                } else {
                    startChatButton.setVisible(false);
                }

                if (e.getClickCount() == 2 && profileController != null) {
                    profileController.showUserProfile(selectedUser);
                }
            }
        });

        bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        startChatButton = new JButton();
        startChatButton.setVisible(false);
        startChatButton.addActionListener(e -> {
            if (selectedFriendId != null && chatRoomController != null) {
                System.out.println("[DEBUG] ChatRoomController 호출됨");
                chatRoomController.openChatRoomDialog(selectedFriendId);
            } else {
                System.err.println("[ERROR] ChatRoomController가 null입니다.");
            }
        });


        bottomPanel.add(startChatButton, BorderLayout.CENTER);

        add(profileContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void updateFriendList(List<String> users) {
        SwingUtilities.invokeLater(() -> {
            friendListModel.clear();
            for (String user : users) {
                friendListModel.addElement(user);
            }
        });
    }

    public void updateMyNickname() {
        User me = UserDatabase.shared().getUserById(userId);
        String nickname = (me != null && me.getNickname() != null) ? me.getNickname() : userId;
        nicknameLabel.setText(nickname);
        introLabel.setText(" ");
    }

    public void setProfilePanel(ProfilePanel profilePanel) {
        this.profilePanel = profilePanel;
    }

    public void setProfileController(ProfileController controller) {
        this.profileController = controller;
    }
    public void setChatRoomController(Controller.ChatRoomController controller) {
        this.chatRoomController = controller;
    }


    public void displayUserInfo(String nickname, String intro, boolean isMine, String imageBase64) {
        String labelText = isMine
                ? "<html>" + nickname + " <span style='color:#66CCCC; font-size:8px;'>내 프로필</span></html>"
                : nickname;
        nicknameLabel.setText(labelText);
        introLabel.setText((intro != null && !intro.isEmpty()) ? intro : "(No introduction)");
        introLabel.setVisible(true);
        setProfileImageFromBase64(imageBase64);
        this.revalidate();
        this.repaint();
    }
    
    public DefaultListModel<String> getFriendListModel() {
        return friendListModel;
    }


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
        try {
            ImageIcon fallback = new ImageIcon(getClass().getResource("/img/logo.png"));
            Image img = fallback.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            profileImg.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
