package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import Controller.MultiChatController;
import model.Message;

public class SchedulePanel extends JPanel {
    private MultiChatController controller;
    private String userId;

    private JTextArea scheduleArea;
    private JTextField dateField;
    private JTextField contentField;

    public SchedulePanel(MultiChatController controller, String userId) {
        this.controller = controller;
        this.userId = userId;

        setLayout(new BorderLayout());

        // 일정 출력 영역
        scheduleArea = new JTextArea();
        scheduleArea.setEditable(false);
        scheduleArea.setLineWrap(true);
        add(new JScrollPane(scheduleArea), BorderLayout.CENTER);

        // 추가 입력 영역
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 1));

        dateField = new JTextField("예: 2025-06-01");
        contentField = new JTextField("일정 내용 입력");

        JButton addBtn = new JButton("일정 추가");
        addBtn.addActionListener(e -> sendSchedule());

        inputPanel.add(dateField);
        inputPanel.add(contentField);
        inputPanel.add(addBtn);

        add(inputPanel, BorderLayout.SOUTH);
    }

    private void sendSchedule() {
        String dateStr = dateField.getText().trim();
        String content = contentField.getText().trim();

        if (dateStr.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "날짜와 내용을 모두 입력하세요.");
            return;
        }

        try {
            // 날짜 유효성 확인
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dateStr);
            
            String combined = "[" + dateStr + "] " + content;

            Message msg = new Message();
            msg.setType("SCHEDULE_ADD");
            msg.setRoomId(0); // 일정용 방
            msg.setId(userId);
            msg.setContent(combined);
            
            if (controller != null) {
                controller.send(msg);
            } else {
                System.err.println("⚠️ SchedulePanel: controller가 연결되지 않았습니다.");
            }

            appendSchedule("📅 추가됨: " + combined);
            dateField.setText("");
            contentField.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "날짜 형식이 잘못되었습니다. yyyy-MM-dd 형식으로 입력하세요.");
        }
    }

    public void appendSchedule(String schedule) {
        scheduleArea.append(schedule + "\n");
    }
}
