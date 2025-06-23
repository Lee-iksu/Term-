package presenter;

import java.text.SimpleDateFormat;
import java.util.List;

import model.Schedule;
import service.DAO.ScheduleDatabase;
import service.DAO.UserDatabase;
import view.schedule.ScheduleView;

public class SchedulePresenter {
    private final ScheduleDatabase database;
    private ScheduleView view;
    private final int roomId; // 각 presenter는 방 하나에 대응

    public SchedulePresenter(int roomId, ScheduleView view) {
        this.roomId = roomId;
        this.database = new ScheduleDatabase(); // DB 접근 객체
        this.view = view;
    }

    public void addSchedule(Schedule s) {
        //database.saveSchedule(s);  
        updateView();              // 다시 전체 로드 후 출력
    }

    public void updateView() {
        List<Schedule> schedules = database.findByRoomId(roomId);
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Schedule s : schedules) {
            // 닉네임 조회
            String creatorNick = UserDatabase.shared().getUserById(s.getCreatorId()) != null
                ? UserDatabase.shared().getUserById(s.getCreatorId()).getNickname()
                : s.getCreatorId();
            String otherNick = UserDatabase.shared().getUserById(s.getOtherId()) != null
                ? UserDatabase.shared().getUserById(s.getOtherId()).getNickname()
                : s.getOtherId();

        }

        view.updateScheduleDisplay(sb.toString());
    }
    
    public void updateViewFromAll() {
        List<Schedule> schedules = database.findAll(); // DB에 있는 일정 전체
        StringBuilder sb = new StringBuilder();

        for (Schedule s : schedules) {
            sb.append(s.getContent()).append("\n");
        }

        view.updateScheduleDisplay(sb.toString());
    }



    public void setView(ScheduleView view) {
        this.view = view;
    }
    
    public int getRoomId() {
        return roomId;
    }

}
