package view.chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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

// 채팅 화면에서 사용자 인터페이스를 제공하는 패널
public class ChatPanel extends JPanel implements ChatView {
    private final String userId;               // 사용자 ID
    private final int roomId;                  // 채팅방 고유 ID
    private final ChatPresenter presenter;     // View-Logic 연결을 위한 Presenter 객체

    private JPanel chatBox;                    // 메시지 출력 영역
    private JScrollPane scrollPane;            // 스크롤 가능한 메시지 영역
    private JTextField inputField;             // 사용자 입력 필드

    // 채팅 화면 UI 초기화 및 레이아웃 구성
    public ChatPanel(ChatPresenter presenter, String userId, int roomId) {
        this.userId = userId;
        this.roomId = roomId;
        this.presenter = presenter;

        // 전체 패널 레이아웃 설정 (상단 메시지, 하단 입력)
        setLayout(new BorderLayout());

        // 메시지들을 수직으로 정렬할 박스 패널 구성
        chatBox = new JPanel();
        chatBox.setLayout(new BoxLayout(chatBox, BoxLayout.Y_AXIS));
        chatBox.setBackground(Color.WHITE);

        // 메시지 박스를 스크롤 가능한 영역에 삽입
        scrollPane = new JScrollPane(chatBox);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // 입력 필드 및 전송 버튼 구성
        inputField = new JTextField();
        JButton sendBtn = new JButton("전송");
        JButton fileBtn = new JButton("📷");          // 이미지 전송 버튼
        JButton calendarBtn = new JButton("📅");      // 일정 공유 버튼

        // 좌측 버튼 영역 크기 설정
        Dimension btnSize = new Dimension(50, 40);
        fileBtn.setPreferredSize(btnSize);
        calendarBtn.setPreferredSize(btnSize);

        // 좌측 버튼들을 수평 정렬
        JPanel leftBtnPanel = new JPanel();
        leftBtnPanel.setLayout(new BoxLayout(leftBtnPanel, BoxLayout.X_AXIS));
        leftBtnPanel.add(fileBtn);
        leftBtnPanel.add(Box.createHorizontalStrut(5));
        leftBtnPanel.add(calendarBtn);

        // 하단 입력 패널 구성 (좌측 버튼, 중앙 입력창, 우측 전송 버튼)
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(leftBtnPanel, BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // 이벤트 리스너 등록 (입력 혹은 버튼 클릭 시 Presenter 호출)
        sendBtn.addActionListener(e -> presenter.onSendButtonClicked(inputField.getText()));
        inputField.addActionListener(e -> presenter.onSendButtonClicked(inputField.getText()));
        fileBtn.addActionListener(e -> presenter.onPhotoSendButtonClicked());
        calendarBtn.addActionListener(e -> presenter.onCalendarButtonClicked());
    }

    // 일반 텍스트 메시지를 UI에 추가
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

        // 자동 스크롤: 가장 아래로 이동
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    // 이미지 메시지 표시
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

    // 시스템 메시지 (예: 알림) 표시
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

    // 채팅방 ID 반환
    @Override
    public int getRoomId() {
        return roomId;
    }

    // 사용자 ID 반환
    @Override
    public String getUserId() {
        return userId;
    }

    // 모든 메시지 제거 (초기화)
    @Override
    public void clearMessages() {
        chatBox.removeAll();
        chatBox.revalidate();
        chatBox.repaint();
    }

    // 입력 필드 초기화
    public void clearInputField() {
        inputField.setText("");
    }
}
