package Handler.server;

import model.User;
import model.Message;
import network.ClientHandler;
import network.ServerCore;
import service.UserDatabase;

public class LoginHandler implements MessageHandler {
    public void handle(Message msg, ClientHandler handler, ServerCore server) {
        handler.setClientId(msg.getId());

        if (UserDatabase.shared().getUserById(msg.getId()) == null) {
            User user = new User(msg.getId(), "");
            user.setNickname(msg.getId());
            user.setIntro("");
            UserDatabase.shared().addUser(user);
        }

        if (!server.getUserList().contains(msg.getId())) {
            server.getUserList().add(msg.getId());
        }

        server.updateUserListBroadcast();
        server.sendAllChatroomsTo(msg.getId());
        server.getUI().log("사용자 " + msg.getId() + " 로그인했습니다.");
    }
}