package network;//Stretgy패턴


import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import Handler.server.CheckHandler;
import Handler.server.CreateGroupRoomHandler;
import Handler.server.CreateRoomHandler;
import Handler.server.GetMessagesHandler;
import Handler.server.LoginHandler;
import Handler.server.LogoutHandler;
import Handler.server.MessageHandler;
import Handler.server.PhotoUploadHandler;
import Handler.server.ProfileRequestHandler;
import Handler.server.ProfileSaveHandler;
import Handler.server.SendMessageHandler;
import model.Message;

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
        handlers.put("CHECK", new CheckHandler());
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
