package network;

/**
 * 메시지 타입 -> 핸들러 연결
 * Strategy 패턴 기반
 * 
 * 들어온 Message 객체
 * msg.getType() 으로 분기
 * 타입에 따라 등록된 handler 실행
 * 
 * static 블록에서 handler 사전 등록
 * key는 문자열 타입 ("SEND_MSG", "CHECK" 등)
 * 
 * handler 없으면 -> broadcast로 전체 전송
 * 
 * if-else 없이 확장 가능하게 설계
 * -> 메시지 타입 추가할 땐 handler만 등록
 */


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
import Handler.server.ScheduleAddHandler;
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
        handlers.put("SCHEDULE_ADD", new ScheduleAddHandler());
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
