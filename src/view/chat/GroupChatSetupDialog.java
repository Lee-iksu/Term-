package view.chat;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class GroupChatSetupDialog extends JDialog {
    private JTextField roomNameField;
    private JList<String> friendList;

    public GroupChatSetupDialog(JFrame parent, List<String> allFriends, BiConsumer<String, List<String>> onCreated) {
        super(parent, "단체 채팅 생성", true);
        setSize(350, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // 제목
        JLabel title = new JLabel("단체 채팅방 설정", JLabel.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        // 친구 선택
        friendList = new JList<>(new DefaultListModel<>());
        friendList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        DefaultListModel<String> model = (DefaultListModel<String>) friendList.getModel();
        for (String name : allFriends) {
            model.addElement(name);
        }

        JScrollPane listScroll = new JScrollPane(friendList);
        listScroll.setPreferredSize(new Dimension(300, 200));

        // 방 이름 입력
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        JLabel nameLabel = new JLabel("방 이름:");
        roomNameField = new JTextField("단체 대화");

        inputPanel.add(nameLabel, BorderLayout.WEST);
        inputPanel.add(roomNameField, BorderLayout.CENTER);

        // 버튼
        JButton createBtn = new JButton("생성");
        createBtn.addActionListener(e -> {
            String roomName = roomNameField.getText().trim();
            List<String> selected = friendList.getSelectedValuesList();

            if (roomName.isEmpty() || selected.isEmpty()) {
                JOptionPane.showMessageDialog(this, "방 이름과 참여자를 모두 선택하세요.");
                return;
            }

            onCreated.accept(roomName, selected);
            dispose();
        });

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        centerPanel.add(listScroll);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(inputPanel);

        add(centerPanel, BorderLayout.CENTER);
        add(createBtn, BorderLayout.SOUTH);
    }
}
