// 채팅방별 스케줄을 텍스트로 표시하는 패널
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
    // 일정 표시용 텍스트 영역 (읽기 전용)
    private final JTextArea scheduleArea = new JTextArea();

    // 채팅방에 따른 일정 로직 제어 객체
    private SchedulePresenter presenter; 

    // 중복 일정 방지용 Set
    private final Set<String> displayedSchedules = new HashSet<>();

    public SchedulePanel() {
        // 전체 레이아웃은 BorderLayout
        setLayout(new BorderLayout());

        // 텍스트 영역 설정
        scheduleArea.setEditable(false);
        scheduleArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        scheduleArea.setMargin(new Insets(10, 10, 10, 10));
        scheduleArea.setLineWrap(true);  // 줄 바꿈 허용

        // 텍스트 영역을 스크롤 가능하게 추가
        add(new JScrollPane(scheduleArea), BorderLayout.CENTER);
        
        // 향후 하단 버튼 패널 (예: 새로고침 버튼) 공간 확보
        JPanel bottomBar = new JPanel();
        bottomBar.setBackground(new Color(102, 204, 204));

        // 🔒 아래 코드는 주석 처리되어 있음 (기능 보류 상태)
        /*
        JButton refreshButton = new JButton("새로고침");
        ...
        add(refreshButton, BorderLayout.SOUTH);
        */
    }

    // 채팅방별 presenter 생성 및 로딩
    public void loadScheduleForRoom(int roomId) {
        this.presenter = new SchedulePresenter(roomId, this);
        presenter.updateView();
    }

    // 스케줄 전체 텍스트 갱신
    @Override
    public void updateScheduleDisplay(String fullText) {
        scheduleArea.setText(fullText);
        System.out.println("[View] 스케줄 텍스트 출력됨:\n" + fullText);
    }

    // 특정 채팅방에 스케줄 추가 요청
    public void addScheduleForRoom(int roomId, Schedule s) {
        if (presenter == null || presenter.getRoomId() != roomId) {
            loadScheduleForRoom(roomId);  // presenter 재설정
        }
        presenter.addSchedule(s);
    }

    // 외부 접근용 presenter 반환
    public SchedulePresenter getPresenter() {
        return presenter;
    }

    // 단일 일정 항목 추가 (중복 방지)
    public void appendSchedule(String content) {
        if (displayedSchedules.contains(content))
            return;

        displayedSchedules.add(content);
        scheduleArea.append(content + "\n");
    }
}
