package Handler;

import model.Message;
import network.ClientHandler;
import network.ServerCore;

public class LogoutHandler implements MessageHandler {
    public void handle(Message msg, ClientHandler handler, ServerCore server) {
        server.removeClient(handler);
        server.getUI().log("사용자 " + msg.getId() + " 로그아웃했습니다.");
    }
}
