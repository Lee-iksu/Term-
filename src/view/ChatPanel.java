package view;

import Controller.MultiChatController;
import model.Message;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatPanel extends JPanel {
    private MultiChatController controller;
    private String userId;
    private int roomId;
    private String targetId;
    
    private JPanel chatBox;
    private JScrollPane scrollPane;
    private JTextField inputField;

    public ChatPanel(MultiChatController controller, String userId, int roomId) {
        this.controller = controller;
        this.userId = userId;
        this.roomId = roomId;
        this.targetId = targetId;

        setLayout(new BorderLayout());

        chatBox = new JPanel();
        chatBox.setLayout(new BoxLayout(chatBox, BoxLayout.Y_AXIS));
        chatBox.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(chatBox);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        inputField = new JTextField();
        JButton sendBtn = new JButton("전송");
        JButton fileBtn = new JButton("📷");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(fileBtn, BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        sendBtn.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
        fileBtn.addActionListener(e -> sendPhoto());

        if (controller != null) {
            Message msg = new Message();
            msg.setType("GET_MESSAGES");
            msg.setRoomId(roomId);
            msg.setId(userId); 
            controller.send(msg);
        }

    }

    private void sendMessage() {
    	System.out.println("[DEBUG] sendMessage() 호출됨");

        String text = inputField.getText().trim();

        if (!text.isEmpty()) {
            inputField.setText("");  
            
            Message msg = new Message();
            msg.setType("SEND_MSG");
            msg.setRoomId(roomId);
            msg.setSender(userId);
            msg.setReceiver(targetId);
            msg.setContent(text);

            if (controller != null)
                controller.send(msg);

            //appendMessage(userId, text, true);
        }
    }

    private void sendPhoto() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String fileName = System.currentTimeMillis() + "_" + file.getName();  // 고유 이름
            
            try {
                // 1. 파일 → 바이트 배열
                byte[] imageBytes = java.nio.file.Files.readAllBytes(file.toPath());

                // 2. Base64 인코딩
                String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);

                // 3. Message 객체 생성
                model.Message msg = new model.Message();
                msg.setType("PHOTO_UPLOAD");
                msg.setSender(userId);
                msg.setRoomId(roomId);
                msg.setContent(base64Image);  // base64 string
                msg.setMsg(fileName);         // 실제 파일명은 msg 필드에 넣자

                // 4. 전송
                controller.send(msg);

                // 5. 채팅창에 표시
                appendMessage(userId, "🖼️ 사진 전송: " + fileName, true);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "이미지 전송 실패: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void appendImageMessage(String sender, ImageIcon imageIcon) {
        boolean isMine = sender.equals(userId);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(Color.WHITE);

        JLabel meta = new JLabel(sender + " | " + new SimpleDateFormat("HH:mm").format(new Date()));
        meta.setFont(new Font("맑은 고딕", Font.ITALIC, 10));
        meta.setForeground(Color.GRAY);
        wrapper.add(meta);
        wrapper.add(Box.createVerticalStrut(3));

        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        wrapper.add(imageLabel);

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setOpaque(false);

        if (isMine) {
            messagePanel.add(wrapper, BorderLayout.EAST);
        } else {
            messagePanel.add(wrapper, BorderLayout.WEST);
        }

        chatBox.add(messagePanel);
        chatBox.add(Box.createVerticalStrut(8));

        revalidate();
        repaint();
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
    }

    public void appendSystemMessage(String msg) {
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setOpaque(false);

        JLabel messageLabel = new JLabel("<html><p style='width: 200px;'>" + msg + "</p></html>");
        messageLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        messageLabel.setOpaque(true);
        messageLabel.setBackground(new Color(255, 255, 200)); // 노란 배경
        messageLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10),
                BorderFactory.createLineBorder(Color.ORANGE, 1)
        ));

        JLabel meta = new JLabel("📢 시스템 알림");
        meta.setFont(new Font("맑은 고딕", Font.ITALIC, 10));
        meta.setForeground(Color.DARK_GRAY);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(Color.WHITE);
        wrapper.add(meta);
        wrapper.add(Box.createVerticalStrut(3));
        wrapper.add(messageLabel);

        messagePanel.add(wrapper, BorderLayout.CENTER); // 가운데 정렬

        chatBox.add(messagePanel);
        chatBox.add(Box.createVerticalStrut(8));

        revalidate();
        repaint();
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
    }


    public void appendMessage(String sender, String content) {
        boolean isMine = sender.equals(userId);
        appendMessage(sender, content, isMine);
    }

    public void appendMessage(String sender, String content, boolean isMine) {
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setOpaque(false);

        JLabel messageLabel = new JLabel("<html><p style='width: 200px;'>" + content + "</p></html>");
        messageLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        messageLabel.setOpaque(true);
        messageLabel.setBackground(new Color(230, 240, 255));
        messageLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10),
                BorderFactory.createLineBorder(new Color(200, 200, 255), 1)
        ));

        // 둥근 모서리
        messageLabel.setBackground(isMine ? new Color(204, 229, 255) : new Color(240, 240, 240));

        // 발신자와 시간
        String timeStr = new SimpleDateFormat("HH:mm").format(new Date());
        JLabel meta = new JLabel(sender + " | " + timeStr);
        meta.setFont(new Font("맑은 고딕", Font.ITALIC, 10));
        meta.setForeground(Color.GRAY);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(Color.WHITE);
        wrapper.add(meta);
        wrapper.add(Box.createVerticalStrut(3));
        wrapper.add(messageLabel);

        if (isMine) {
            messagePanel.add(wrapper, BorderLayout.EAST);
        } else {
            messagePanel.add(wrapper, BorderLayout.WEST);
        }

        chatBox.add(messagePanel);
        chatBox.add(Box.createVerticalStrut(5));

        revalidate();
        repaint();
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    public int getRoomId() {
        return roomId;
    }
}
