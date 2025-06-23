package Handler.server;

import model.Message;
import network.ClientHandler;
import network.ServerCore;

public class LogoutHandler implements MessageHandler {
    // 로그아웃 요청 처리

    public void handle(Message msg, ClientHandler handler, ServerCore server) {
        server.removeClient(handler); // 서버에서 핸들러 제거
        server.getUI().log("사용자 " + msg.getId() + " 로그아웃했습니다.");
    }
}
