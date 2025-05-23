package view;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatFrame extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;

    public ChatFrame(String userId) {
        setTitle("채팅방 - " + userId);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField();
        sendButton = new JButton("전송");

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage(userId));
        inputField.addActionListener(e -> sendMessage(userId));

        setVisible(true);
    }

    private void sendMessage(String userId) {
        String msg = inputField.getText();
        if (!msg.isEmpty()) {
            chatArea.append(userId + ": " + msg + "\n");
            inputField.setText("");
        }
    }
}