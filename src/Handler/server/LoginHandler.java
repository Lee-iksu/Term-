package Handler.server;

import java.text.SimpleDateFormat;
import java.util.List;

import com.google.gson.Gson;

import model.Message;
import model.Schedule;
import model.User;
import network.ClientHandler;
import network.ServerCore;
import service.DAO.ScheduleDatabase;
import service.DAO.UserDatabase;

public class LoginHandler implements MessageHandler {
    // 로그인 요청 처리
    // 사용자 등록 + 접속 목록 관리 + 일정 전송

    public void handle(Message msg, ClientHandler handler, ServerCore server) {
        server.removeClientById(msg.getId());      // 중복 로그인 방지
        handler.setClientId(msg.getId());          // handler에 ID 연결

        // DB에 사용자 정보 없으면 새로 등록
        if (UserDatabase.shared().getUserById(msg.getId()) == null) {
            User user = new User(msg.getId(), "");
            user.setNickname(msg.getId());
            user.setIntro("");
            UserDatabase.shared().addUser(user);
        }

        // 접속자 목록에 추가
        if (!server.getUserList().contains(msg.getId()))
            server.getUserList().add(msg.getId());

        // 접속자 목록 -> 전체에게 브로드캐스트
        server.updateUserListBroadcast();

        // 채팅방 정보 -> 이 유저에게만 전송
        server.sendAllChatroomsTo(msg.getId());

        // UI 로그 출력
        server.getUI().log("사용자 " + msg.getId() + " 로그인했습니다.");

        // 일정 정보 전체 전송 (DB 조회만, 저장 안 함)
        List<Schedule> schedules = new ScheduleDatabase().findAll();
        Gson gson = new Gson();

        for (Schedule s : schedules) {
            Message scheduleMsg = new Message();
            scheduleMsg.setType("SCHEDULE_ADD");
            scheduleMsg.setRoomId(0);
            scheduleMsg.setId(s.getCreatorId());
            scheduleMsg.setContent(s.getContent());
            handler.send(gson.toJson(scheduleMsg));
        }
    }
}
