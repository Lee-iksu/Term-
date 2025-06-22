package service;

import model.Schedule;
import java.util.List;

public class ScheduleService {
    private final ScheduleDatabase db = new ScheduleDatabase();

    public void addSchedule(Schedule s) {
        db.saveSchedule(s); // DB 저장
    }

    public List<Schedule> getSchedulesForRoom(int roomId) {
        return db.findByRoomId(roomId); // DB에서 일정 목록 가져오기
    }
}
