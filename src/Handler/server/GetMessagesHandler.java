package Handler.server;

import java.text.SimpleDateFormat;

import com.google.gson.Gson;

import model.ChatMessage;
import model.Chatroom;
import model.Message;
import network.ClientHandler;
import network.ServerCore;

public class GetMessagesHandler implements MessageHandler {
    // 채팅방 내 과거 메시지 요청 처리

    public void handle(Message msg, ClientHandler handler, ServerCore server) {
        Gson gson = new Gson();
        int roomId = msg.getRoomId();
        String userId = msg.getId();

        Chatroom room = server.getChatrooms().get(roomId);

        if (room != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

            // 모든 메시지 하나씩 보내줌
            for (ChatMessage cm : room.getMessages()) {
                Message historyMsg = new Message();
                historyMsg.setType("HISTORY_MSG");
                historyMsg.setSender(cm.getSender());
                historyMsg.setRoomId(roomId);
                historyMsg.setContent(cm.getContent());
                historyMsg.setTimestamp(cm.getTimestamp());

                server.sendTo(userId, gson.toJson(historyMsg));
            }
        }
    }
}
