package view.friend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Base64;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import model.User;
import presenter.FriendPresenter;
import service.DAO.UserDatabase;

public class FriendPanel extends JPanel implements FriendView {
    private DefaultListModel<String> friendListModel = new DefaultListModel<>();
    private JList<String> friendList = new JList<>(friendListModel);
    private JLabel nicknameLabel;
    private JLabel introLabel;
    private JLabel profileImg;
    private JButton startChatButton;
    private String userId;

    private FriendPresenter presenter;

    public FriendPanel(String userId) {
        this.userId = userId;
        this.presenter = new FriendPresenter(this, userId);

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        User me = UserDatabase.shared().getUserById(userId);
        String nickname = (me != null && me.getNickname() != null) ? me.getNickname() : userId;
        String imageBase64 = (me != null) ? me.getImageBase64() : "";

        profileImg = new JLabel();
        profileImg.setPreferredSize(new Dimension(50, 50));
        setProfileImageFromBase64(imageBase64);

        nicknameLabel = new JLabel(nickname);
        nicknameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        introLabel = new JLabel(" ");
        introLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        introLabel.setForeground(Color.DARK_GRAY);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(nicknameLabel);
        textPanel.add(introLabel);

        JPanel myProfilePanel = new JPanel(new BorderLayout(10, 0));
        myProfilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        myProfilePanel.setBackground(Color.WHITE);
        myProfilePanel.add(profileImg, BorderLayout.WEST);
        myProfilePanel.add(textPanel, BorderLayout.CENTER);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(200, 200, 200));

        JPanel profileContainer = new JPanel(new BorderLayout());
        profileContainer.setBackground(Color.WHITE);
        profileContainer.add(myProfilePanel, BorderLayout.CENTER);
        profileContainer.add(separator, BorderLayout.SOUTH);

        friendList.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        friendList.setSelectionBackground(new Color(220, 240, 255));
        friendList.setBackground(Color.WHITE);
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

        startChatButton = new JButton();
        startChatButton.setVisible(false);
        
        startChatButton.addActionListener(e -> {
            presenter.onStartChatButtonClicked();
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        bottomPanel.add(startChatButton, BorderLayout.CENTER);

        add(profileContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    @Override
    public void showFriendList(List<String> users) {
        SwingUtilities.invokeLater(() -> {
            friendListModel.clear();
            for (String user : users)
                friendListModel.addElement(user);
        });
    }

    @Override
    public void showUserProfile(String nickname, String intro, String imageBase64, boolean isMine) {
        String labelText = isMine ? "<html>" + nickname + " <span style='color:#66CCCC; font-size:8px;'>내 프로필</span></html>" : nickname;
        nicknameLabel.setText(labelText);
        introLabel.setText((intro != null && !intro.isEmpty()) ? intro : "(No introduction)");
        setProfileImageFromBase64(imageBase64);
    }

    @Override
    public void updateMyInfo(String nickname) {
        nicknameLabel.setText(nickname);
        introLabel.setText(" ");
    }

    @Override
    public void enableChatButton(String friendId) {
        startChatButton.setText(friendId + "님과 채팅방 생성");
        startChatButton.setVisible(true);
    }

    @Override
    public void disableChatButton() {
        startChatButton.setVisible(false);
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
    
    public FriendPresenter getPresenter() {
        return presenter;
    }
    
    public String getUserId() {
        return this.userId;
    }

}
