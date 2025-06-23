package Handler.client;

import java.text.SimpleDateFormat;
import javax.swing.SwingUtilities;

import model.Chatroom;
import model.Message;
import model.Schedule;
import model.User;
import service.UserDatabase;
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

        String content = msg.getContent(); // [2025-06-25] 발표 준비
        int roomId = msg.getRoomId();      // 일정이 속한 채팅방
        String myId = mainFrame.getUserId();

        try {
            // 상대방 아이디 추출
            Chatroom room = mainFrame.getChatRoomController().getChatroomById(roomId);
            if (room == null) {
                System.err.println("[오류] 해당 roomId(" + roomId + ")의 Chatroom이 없습니다");
                return;
            }

            String otherId = room.getMembers().stream()
                .filter(id -> !id.equals(myId))
                .findFirst()
                .orElse("알 수 없음");

            if (msg.getId().equals(otherId)) {
                System.out.println("[DEBUG] 자기 자신과의 일정은 무시됨");
                return;
            }

            // 날짜와 내용 추출
            String dateStr = content.substring(content.indexOf("[") + 1, content.indexOf("]")).trim();
            String body = content.substring(content.indexOf("]") + 1).trim();

            // [날짜] 내용 형식 그대로 저장
            Schedule s = new Schedule();
            s.setRoomId(roomId);
            s.setCreatorId(msg.getId());
            s.setOtherId(otherId);
            s.setContent("[" + dateStr + "] " + body); // ✅ 이 한 줄만 content로 저장
            s.setScheduleDate(new SimpleDateFormat("yyyy-MM-dd").parse(dateStr));

            SwingUtilities.invokeLater(() -> {
                SchedulePanel sp = mainFrame.getSchedulePanel();
                sp.loadScheduleForRoom(roomId);
                sp.getPresenter().addSchedule(s);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
