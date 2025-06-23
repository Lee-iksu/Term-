package Handler.client;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.SwingUtilities;

import model.Message;
import model.Schedule;
import service.ScheduleDatabase;
import view.MainFrame;
import view.SchedulePanel;

public class ScheduleAddHandler implements MessageHandler {
    private final MainFrame mainFrame;

    public ScheduleAddHandler(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public void handle(Message msg) {
        System.out.println("[DEBUG] ScheduleAddHandler 수신됨: " + msg.getContent());

        String content = msg.getContent(); // 예: [2025-06-25] 발표 준비
        int roomId = msg.getRoomId();
        String creatorId = msg.getId();  // 보내는 사람
        String myId = mainFrame.getUserId(); // 로그인한 나

        try {
            // 날짜, 본문 파싱
            String dateStr = content.substring(content.indexOf("[") + 1, content.indexOf("]")).trim();
            String body = content.substring(content.indexOf("]") + 1).trim();

            // Schedule 객체 생성
            Schedule s = new Schedule();
            s.setRoomId(roomId);
            s.setCreatorId(creatorId);
            s.setOtherId(myId.equals(creatorId) ? "알 수 없음" : myId); // 상대방
            s.setScheduleDate(new SimpleDateFormat("yyyy-MM-dd").parse(dateStr));
            s.setContent("[" + dateStr + "] " + body); // 그대로 저장

            List<Schedule> all = new ScheduleDatabase().findAll();
            boolean alreadyExists = all.stream().anyMatch(sch ->
                sch.getCreatorId().equals(creatorId) &&
                sch.getContent().equals(s.getContent())
            );

            System.out.println("[DEBUG] creatorId=" + creatorId + ", roomId=" + roomId + ", content=" + s.getContent());
            System.out.println("[DEBUG] alreadyExists=" + alreadyExists);

            if (!alreadyExists && roomId != 0)
                new ScheduleDatabase().saveSchedule(s);


            // UI에 추가
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
