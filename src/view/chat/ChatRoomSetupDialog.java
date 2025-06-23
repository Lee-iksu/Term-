package view.chat;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class ChatRoomSetupDialog extends JDialog {
    public ChatRoomSetupDialog(JFrame parent, String targetId, Consumer<String> onRoomCreated) {
        super(parent, "채팅방 설정", true);
        setSize(300, 150);
        setLocationRelativeTo(parent);

        setLayout(new BorderLayout(10, 10));

        JLabel label = new JLabel("채팅방 이름 입력:");
        JTextField roomNameField = new JTextField(targetId + "님과의 대화");

        JButton createButton = new JButton("생성");
        createButton.addActionListener(e -> {
            String roomName = roomNameField.getText().trim();
            if (!roomName.isEmpty()) {
                onRoomCreated.accept(roomName);
                dispose();
            }
        });

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.add(label, BorderLayout.NORTH);
        inputPanel.add(roomNameField, BorderLayout.CENTER);
        inputPanel.add(createButton, BorderLayout.SOUTH);

        add(inputPanel, BorderLayout.CENTER);
    }
}
