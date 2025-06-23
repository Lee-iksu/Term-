package Handler.server;

import com.google.gson.Gson;

import model.Message;
import network.ClientHandler;
import network.ServerCore;

public class ScheduleAddHandler implements MessageHandler {
    // 일정 메시지 -> 브로드캐스트
    public void handle(Message msg, ClientHandler handler, ServerCore server) {
        server.broadcast(new Gson().toJson(msg)); // 전체에게 전송
    }
}
