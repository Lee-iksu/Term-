package view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import model.Schedule;
import presenter.SchedulePresenter;

public class SchedulePanel extends JPanel implements ScheduleView {
    private final JTextArea scheduleArea = new JTextArea();
    private SchedulePresenter presenter; // ← 이제는 setter로 바꿀 거야

    public SchedulePanel() {
        setLayout(new BorderLayout());

        scheduleArea.setEditable(false);
        scheduleArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        scheduleArea.setMargin(new Insets(10, 10, 10, 10));
        scheduleArea.setLineWrap(true);

        add(new JScrollPane(scheduleArea), BorderLayout.CENTER);
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

}
