package Handler.server;

import com.google.gson.Gson;

import model.Message;
import network.ClientHandler;
import network.ServerCore;

public class ScheduleAddHandler implements MessageHandler {
    @Override
    public void handle(Message msg, ClientHandler handler, ServerCore server) {
        // 받은 일정 메시지를 전체에게 브로드캐스트
    	server.broadcast(new Gson().toJson(msg));

    }
}