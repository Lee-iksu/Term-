package Handler.server;

import java.util.List;

import com.google.gson.Gson;

import model.Message;
import network.ClientHandler;
import network.ServerCore;

public class CheckHandler implements MessageHandler {
    // 클라이언트가 CHECK 요청 보냈을 때 -> 서버가 처리
    // 현재 접속자 목록 반환

    @Override
    public void handle(Message msg, ClientHandler handler, ServerCore server) {
        List<String> userList = server.getUserList(); // 현재 접속중인 유저 ID 목록

        Message response = new Message();
        response.setType("CHECK");         // 응답 타입
        response.setId("server");          // 서버가 보냄
        response.setRcvid(msg.getId());    // 요청자에게 보냄
        response.setCheck(userList);       // 리스트 담기

        String json = new Gson().toJson(response); // JSON 변환
        handler.send(json); // 응답 전송
    }
}
