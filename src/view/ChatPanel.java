package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import presenter.ChatPresenter;

public class ChatPanel extends JPanel implements ChatView {
    private final String userId;
    private final int roomId;
    private final ChatPresenter presenter;

    private JPanel chatBox;
    private JScrollPane scrollPane;
    private JTextField inputField;

    public ChatPanel(ChatPresenter presenter, String userId, int roomId) {
        this.userId = userId;
        this.roomId = roomId;
        this.presenter = presenter;

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

        sendBtn.addActionListener(e -> presenter.onSendButtonClicked(inputField.getText()));
        inputField.addActionListener(e -> presenter.onSendButtonClicked(inputField.getText()));
        fileBtn.addActionListener(e -> presenter.onPhotoSendButtonClicked());
    }

    // 구현된 ChatView 메서드
    @Override
    public void appendMessage(String sender, String content, boolean isMine) {
        JLabel messageLabel = new JLabel("<html><p style='width: 200px;'>" + content + "</p></html>");
        messageLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        messageLabel.setOpaque(true);
        messageLabel.setBackground(isMine ? new Color(204, 229, 255) : new Color(240, 240, 240));
        messageLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 10, 5, 10),
            BorderFactory.createLineBorder(new Color(200, 200, 255), 1)
        ));

        JLabel meta = new JLabel(sender + " | " + new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date()));
        meta.setFont(new Font("맑은 고딕", Font.ITALIC, 10));
        meta.setForeground(Color.GRAY);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(Color.WHITE);
        wrapper.add(meta);
        wrapper.add(Box.createVerticalStrut(3));
        wrapper.add(messageLabel);

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setOpaque(false);
        messagePanel.add(wrapper, isMine ? BorderLayout.EAST : BorderLayout.WEST);

        chatBox.add(messagePanel);
        chatBox.add(Box.createVerticalStrut(5));
        revalidate();
        repaint();
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        
        SwingUtilities.invokeLater(() -> { //스크롤제일 하단으로
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    
    }

    @Override
    public void appendImageMessage(String sender, ImageIcon imageIcon) {
        boolean isMine = sender.equals(userId);

        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel meta = new JLabel(sender + " | " + new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date()));
        meta.setFont(new Font("맑은 고딕", Font.ITALIC, 10));
        meta.setForeground(Color.GRAY);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(Color.WHITE);
        wrapper.add(meta);
        wrapper.add(Box.createVerticalStrut(3));
        wrapper.add(imageLabel);

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setOpaque(false);
        messagePanel.add(wrapper, isMine ? BorderLayout.EAST : BorderLayout.WEST);

        chatBox.add(messagePanel);
        chatBox.add(Box.createVerticalStrut(8));
        revalidate();
        repaint();
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
    }

    @Override
    public void appendSystemMessage(String msg) {
        JLabel messageLabel = new JLabel("<html><p style='width: 200px;'>" + msg + "</p></html>");
        messageLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        messageLabel.setOpaque(true);
        messageLabel.setBackground(new Color(255, 255, 200));
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

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setOpaque(false);
        messagePanel.add(wrapper, BorderLayout.CENTER);

        chatBox.add(messagePanel);
        chatBox.add(Box.createVerticalStrut(8));
        revalidate();
        repaint();
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
    }

    @Override
    public int getRoomId() {
        return roomId;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    public void clearInputField() {
        inputField.setText("");
    }
}
