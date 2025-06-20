package Handler;

import com.google.gson.Gson;
import model.Chatroom;
import model.Message;
import network.ClientHandler;
import network.ServerCore;

import java.util.Arrays;

public class CreateRoomHandler implements MessageHandler {
    public void handle(Message msg, ClientHandler handler, ServerCore server) {
        Gson gson = new Gson();
        try {
            String senderId = msg.getArgs()[0];
            String targetId = msg.getArgs()[1];
            String roomName = msg.getArgs()[2];

            int roomId = (int) (System.currentTimeMillis() % 100000);

            Chatroom room = new Chatroom();
            room.setId(roomId);
            room.setName(roomName);
            room.setGroup(false);
            room.setMembers(Arrays.asList(senderId, targetId));
            server.getChatrooms().put(roomId, room);

            Message roomCreatedMsg1 = new Message();
            roomCreatedMsg1.setType("ROOM_CREATED");
            roomCreatedMsg1.setId("server");
            roomCreatedMsg1.setRcvid(senderId);
            roomCreatedMsg1.setArgs(new String[]{String.valueOf(roomId), roomName, targetId});

            Message roomCreatedMsg2 = new Message();
            roomCreatedMsg2.setType("ROOM_CREATED");
            roomCreatedMsg2.setId("server");
            roomCreatedMsg2.setRcvid(targetId);
            roomCreatedMsg2.setArgs(new String[]{String.valueOf(roomId), roomName, senderId});

            server.sendTo(senderId, gson.toJson(roomCreatedMsg1));
            server.sendTo(targetId, gson.toJson(roomCreatedMsg2));

            server.getUI().log("[DEBUG] ROOM_CREATED 전송 완료: " + roomName + " (" + roomId + ")");
        } catch (Exception e) {
            System.err.println("[ERROR] CREATE_ROOM 처리 중 예외 발생");
            e.printStackTrace();
        }
    }
}
