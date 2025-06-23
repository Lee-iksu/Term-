package view.schedule;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import model.Schedule;
import presenter.SchedulePresenter;

public class SchedulePanel extends JPanel implements ScheduleView {
    private final JTextArea scheduleArea = new JTextArea();
    private SchedulePresenter presenter; 
    private final Set<String> displayedSchedules = new HashSet<>();

    public SchedulePanel() {
        setLayout(new BorderLayout());

        scheduleArea.setEditable(false);
        scheduleArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        scheduleArea.setMargin(new Insets(10, 10, 10, 10));
        scheduleArea.setLineWrap(true);

        add(new JScrollPane(scheduleArea), BorderLayout.CENTER);
        
        JPanel bottomBar = new JPanel();
        bottomBar.setBackground(new Color(102, 204, 204));

        /*JButton refreshButton = new JButton("새로고침");
        refreshButton.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        refreshButton.setFocusPainted(false);
        refreshButton.setMargin(new Insets(3, 8, 3, 8));
        refreshButton.setBackground(new Color(102, 204, 204)); // ✅ 버튼만 민트색
        refreshButton.setForeground(Color.WHITE);  
        
        refreshButton.addActionListener(e -> {
            if (presenter != null)
                presenter.updateViewFromAll(); // ✅ 전체 불러오기용으로 변경
        });
ㅅ

        add(refreshButton, BorderLayout.SOUTH); // 그냥 아래 붙이기*/
    }

    public void loadScheduleForRoom(int roomId) {
        this.presenter = new SchedulePresenter(roomId, this); // ← 매번 presenter 새로 생성
        presenter.updateView();
    }

    @Override
    public void updateScheduleDisplay(String fullText) {
        scheduleArea.setText(fullText);
        System.out.println("[View] 스케줄 텍스트 출력됨:\n" + fullText);
    }

    public void addScheduleForRoom(int roomId, Schedule s) {
        if (presenter == null || presenter.getRoomId() != roomId) {
            loadScheduleForRoom(roomId); // presenter 초기화
        }
        presenter.addSchedule(s);
    }
    
    public SchedulePresenter getPresenter() {
        return presenter;
    }
    
    public void appendSchedule(String content) {
        if (displayedSchedules.contains(content))
            return; // 이미 출력된 일정이면 무시

        displayedSchedules.add(content);
        scheduleArea.append(content + "\n");
    }


}
