package model;

import java.util.ArrayList;
import java.util.List;

import service.ScheduleDatabase;

public class ScheduleModel {
    private final List<Schedule> schedules = new ArrayList<>();
    private final ScheduleDatabase db;
    
    public ScheduleModel(ScheduleDatabase db) {
        this.db = db;
    }

    public void addSchedule(Schedule s) {
        schedules.add(s);
        db.saveSchedule(s);
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }
    
    public void loadSchedules(int roomId) {
        schedules.clear();
        schedules.addAll(db.findByRoomId(roomId));
    }
    
}
