package Handler.client;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.SwingUtilities;

import model.Message;
import model.Schedule;
import service.DAO.ScheduleDatabase;
import view.main.MainFrame;
import view.schedule.SchedulePanel;

public class ScheduleAddHandler implements MessageHandler {
    // 일정 추가 메시지 처리
    // 서버에서 일정 도착 시 DB 저장 + UI 반영

    private final MainFrame mainFrame;

    public ScheduleAddHandler(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public void handle(Message msg) {
        String content = msg.getContent(); // [날짜] 내용
        int roomId = msg.getRoomId();
        String creatorId = msg.getId();  // 보내는 사람
        String myId = mainFrame.getUserId(); // 현재 사용자

        try {
            // 날짜와 본문 파싱
            String dateStr = content.substring(content.indexOf("[") + 1, content.indexOf("]")).trim();
            String body = content.substring(content.indexOf("]") + 1).trim();

            // Schedule 객체 생성
            Schedule s = new Schedule();
            s.setRoomId(roomId);
            s.setCreatorId(creatorId);
            s.setOtherId(myId.equals(creatorId) ? "알 수 없음" : myId);
            s.setScheduleDate(new SimpleDateFormat("yyyy-MM-dd").parse(dateStr));
            s.setContent("[" + dateStr + "] " + body);

            // 중복 방지 확인
            List<Schedule> all = new ScheduleDatabase().findAll();
            boolean alreadyExists = all.stream().anyMatch(sch ->
                sch.getCreatorId().equals(creatorId) &&
                sch.getContent().equals(s.getContent())
            );


            // 조건 만족 시 저장
            if (!alreadyExists && roomId != 0)
                new ScheduleDatabase().saveSchedule(s);

            // UI에 표시
            SwingUtilities.invokeLater(() -> {
                SchedulePanel sp = mainFrame.getSchedulePanel();
                if (sp != null)
                    sp.appendSchedule(s.getContent());
                else
                    System.err.println("[오류] SchedulePanel이 null입니다");
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
