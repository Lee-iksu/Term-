package view;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import Controller.MultiChatController;

public class ChatPanel extends JPanel {
    private MultiChatController controller;
    private String userId;
    private int roomId;

    private JTextArea chatArea;
    private JTextField inputField;

    public ChatPanel(String roomName, String targetId) {
        // 원하는 필드 초기화하거나 로그 찍기
        this.setLayout(new BorderLayout());

        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(chatArea);

        JTextField inputField = new JTextField();
        JButton sendButton = new JButton("전송");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        this.add(scroll, BorderLayout.CENTER);
        this.add(inputPanel, BorderLayout.SOUTH);

        // 예시 로그
        System.out.println("[ChatPanel 생성됨] 대화상대: " + targetId + ", 방이름: " + roomName);
    }


    public ChatPanel(MultiChatController controller, String userId, int roomId) {
        this.controller = controller;
        this.userId = userId;
        this.roomId = roomId;

        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(chatArea);
        add(scroll, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JButton sendBtn = new JButton("전송");
        JButton fileBtn = new JButton("📷");

        inputPanel.add(fileBtn, BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        sendBtn.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
        fileBtn.addActionListener(e -> sendPhoto());

        if (controller != null) {
            controller.send("GET_MESSAGES|" + roomId);
        }
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            String protocol = String.format("SEND_MSG|%d|%s|%s", roomId, userId, text);
            if (controller != null) {
                controller.send(protocol);
            }
            inputField.setText("");
            appendMessage(userId, text);
        }
    }

    private void sendPhoto() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String fileName = file.getName();
            String protocol = String.format("PHOTO_UPLOAD|%d|%s|%s", roomId, userId, fileName);
            if (controller != null) {
                controller.send(protocol);
            }
            appendMessage(userId, "🖼️ [사진 업로드]: " + fileName);
        }
    }

    
    public void appendMessage(String sender, String content) {
        chatArea.append(sender + ": " + content + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public void appendMessage(String msg) {
        chatArea.append(msg + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
}
