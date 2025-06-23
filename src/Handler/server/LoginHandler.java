package Handler.server;

import java.text.SimpleDateFormat;
import java.util.List;

import com.google.gson.Gson;

import model.Message;
import model.Schedule;
import model.User;
import network.ClientHandler;
import network.ServerCore;
import service.ScheduleDatabase;
import service.UserDatabase;

public class LoginHandler implements MessageHandler {
    public void handle(Message msg, ClientHandler handler, ServerCore server) {
        server.removeClientById(msg.getId());
        handler.setClientId(msg.getId());

        // 사용자 등록
        if (UserDatabase.shared().getUserById(msg.getId()) == null) {
            User user = new User(msg.getId(), "");
            user.setNickname(msg.getId());
            user.setIntro("");
            UserDatabase.shared().addUser(user);
        }

        // 접속자 목록 등록
        if (!server.getUserList().contains(msg.getId())) {
            server.getUserList().add(msg.getId());
        }

        server.updateUserListBroadcast();
        server.sendAllChatroomsTo(msg.getId());
        server.getUI().log("사용자 " + msg.getId() + " 로그인했습니다.");

        // ✅ 일정은 전송만. 저장 안 함
        ScheduleDatabase db = new ScheduleDatabase(); // shared() 안 씀
        List<Schedule> schedules = db.findAll();
        Gson gson = new Gson();

        for (Schedule s : schedules) {
            Message scheduleMsg = new Message();
            scheduleMsg.setType("SCHEDULE_ADD");
            scheduleMsg.setRoomId(0);
            scheduleMsg.setId(s.getCreatorId());
            scheduleMsg.setContent(s.getContent());

            handler.send(gson.toJson(scheduleMsg)); // JSON 전송
        }
    }
}
