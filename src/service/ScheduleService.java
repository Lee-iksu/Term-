package service;

/**
 * 일정 관련 비즈니스 로직 처리 클래스
 * 
 * - DAO(ScheduleDatabase)를 내부에서 호출
 * - UI나 컨트롤러에서 직접 DB를 건드리지 않게 분리
 * 
 * 책임: 저장/조회 요청을 받아서 DAO에 위임하는 계층
 * 
 * - DB 처리 로직은 ScheduleDatabase(ScheduleDAO)가 담당
 */


import model.Schedule;
import service.DAO.ScheduleDatabase;
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
