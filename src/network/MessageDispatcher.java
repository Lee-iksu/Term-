package network;//Stretgy패턴

import com.google.gson.Gson;
import Handler.*;
import model.Message;

import java.util.HashMap;
import java.util.Map;

public class MessageDispatcher {
    private static final Map<String, MessageHandler> handlers = new HashMap<>();

    static {
        handlers.put("login", new LoginHandler());
        handlers.put("logout", new LogoutHandler());
        handlers.put("PROFILE_REQUEST", new ProfileRequestHandler());
        handlers.put("PHOTO_UPLOAD", new PhotoUploadHandler());
        handlers.put("PROFILE_SAVE", new ProfileSaveHandler());
        handlers.put("SEND_MSG", new SendMessageHandler());
        handlers.put("GET_MESSAGES", new GetMessagesHandler());
        handlers.put("CREATE_ROOM", new CreateRoomHandler());
        handlers.put("CREATE_GROUP_ROOM", new CreateGroupRoomHandler());
    }

    public static void dispatch(Message msg, ClientHandler handler, ServerCore server) {
        MessageHandler h = handlers.get(msg.getType());
        if (h != null) {
            h.handle(msg, handler, server);
        } else {
            server.broadcast(new Gson().toJson(msg));
            server.getUI().log(msg.getId() + " : " + msg.getMsg());
        }
    }
}
