package presenter;

import java.text.SimpleDateFormat;
import java.util.List;

import model.Schedule;
import service.DAO.ScheduleDatabase;
import service.DAO.UserDatabase;
import view.schedule.ScheduleView;

public class SchedulePresenter {
    // 일정 데이터를 다루는 DAO 객체
    private final ScheduleDatabase database;

    // 일정 관련 화면 구성 및 출력 뷰 객체
    private ScheduleView view;

    // 이 Presenter가 담당하는 채팅방의 고유 ID
    private final int roomId;

    // 생성자: 채팅방 ID와 뷰를 전달받아 초기화
    public SchedulePresenter(int roomId, ScheduleView view) {
        this.roomId = roomId;
        this.database = new ScheduleDatabase(); // DB 접근 객체 생성
        this.view = view;
    }

    // 일정 추가 시 호출되는 메서드
    public void addSchedule(Schedule s) {
        // 일정 저장 로직은 주석 처리되어 있지만, 실제 구현에서는 아래처럼 저장할 수 있음
        // database.saveSchedule(s);

        // 저장 후 전체 일정 목록을 다시 뷰에 반영
        updateView();
    }

    // 현재 채팅방(roomId)에 해당하는 일정만 불러와 뷰에 표시하는 메서드
    public void updateView() {
        List<Schedule> schedules = database.findByRoomId(roomId);  // 해당 방 ID로 일정 검색
        StringBuilder sb = new StringBuilder();                    // 결과 출력용 버퍼
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 날짜 포맷 지정

        for (Schedule s : schedules) {
            // 일정 작성자의 닉네임 조회 (없으면 ID 그대로 사용)
            String creatorNick = UserDatabase.shared().getUserById(s.getCreatorId()) != null
                ? UserDatabase.shared().getUserById(s.getCreatorId()).getNickname()
                : s.getCreatorId();

            // 상대방 닉네임 조회
            String otherNick = UserDatabase.shared().getUserById(s.getOtherId()) != null
                ? UserDatabase.shared().getUserById(s.getOtherId()).getNickname()
                : s.getOtherId();

            // 이 부분에서 일정 내용을 포맷팅해 sb.append(...) 해야 하나 누락되어 있음
            // sb.append(...) 를 통해 일정을 문자열로 뷰에 전달해야 함
        }

        // 구성된 문자열을 뷰에 전달하여 화면 갱신
        view.updateScheduleDisplay(sb.toString());
    }

    // 전체 방의 일정을 불러와 표시하는 메서드 (관리자용 등)
    public void updateViewFromAll() {
        List<Schedule> schedules = database.findAll();         // 전체 일정 조회
        StringBuilder sb = new StringBuilder();                // 출력용 버퍼

        for (Schedule s : schedules) {
            sb.append(s.getContent()).append("\n");            // 일정 내용을 한 줄씩 추가
        }

        view.updateScheduleDisplay(sb.toString());             // 출력
    }

    // 외부에서 뷰 객체를 다시 설정할 수 있도록 제공
    public void setView(ScheduleView view) {
        this.view = view;
    }

    // 이 Presenter가 관리하는 방 ID를 반환
    public int getRoomId() {
        return roomId;
    }
}
