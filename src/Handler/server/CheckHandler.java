package Handler.server;

import java.util.List;

import com.google.gson.Gson;

import model.Message;
import network.ClientHandler;
import network.ServerCore;

public class CheckHandler implements MessageHandler {

    @Override
    public void handle(Message msg, ClientHandler handler, ServerCore server) {
        List<String> userList = server.getUserList();

        Message response = new Message();
        response.setType("CHECK");
        response.setId("server");
        response.setRcvid(msg.getId()); // 요청자 ID
        response.setCheck(userList);    // 친구 목록(또는 접속자 목록)

        String json = new Gson().toJson(response);
        
        System.out.println("[DEBUG] CHECK 요청 처리, 유저리스트: " + userList);
        System.out.println("[DEBUG] 응답 JSON: " + json);

        System.out.println("[DEBUG] CHECK 응답 JSON: " + json);
        handler.send(json);
    }
}
