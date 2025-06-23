package Handler.server;

import com.google.gson.Gson;

import model.Chatroom;
import model.Message;
import network.ClientHandler;
import network.ServerCore;

import java.util.Arrays;
import java.util.List;

public class CreateGroupRoomHandler implements MessageHandler {
    // 그룹 채팅방 생성 요청 처리
    // 방 만들고 참여자들한테 알림 전송

    public void handle(Message msg, ClientHandler handler, ServerCore server) {
        Gson gson = new Gson();
        String msgStr = msg.getMsg();

        if (msgStr == null || msgStr.split("\\|").length < 3) {
            handler.send("ERROR|메시지 형식 오류");
            return;
        }

        String[] tokens = msgStr.split("\\|");
        String roomName = tokens[1];
        List<String> participants = Arrays.asList(tokens[2].split(","));
        int roomId = (int) (System.currentTimeMillis() % 100000);

        if (server.roomExists(roomName)) {
            handler.send("ERROR|이미 존재하는 방 이름입니다.");
            return;
        }

        Chatroom room = new Chatroom();
        room.setId(roomId);
        room.setName(roomName);
        room.setGroup(true);
        room.setMembers(participants);
        server.getChatrooms().put(roomId, room);

        server.createGroupChatRoom(roomName, participants);

        // 참여자 모두에게 ROOM_CREATED 메시지 전송
        for (String participantId : participants) {
            ClientHandler targetHandler = server.getClientHandler(participantId);
            if (targetHandler != null) {
                Message roomCreatedMsg = new Message();
                roomCreatedMsg.setType("ROOM_CREATED");
                roomCreatedMsg.setId("server");
                roomCreatedMsg.setArgs(new String[]{
                    String.valueOf(roomId), roomName, "GROUP"
                });
                targetHandler.send(gson.toJson(roomCreatedMsg));
            }
        }
    }
}
