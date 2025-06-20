package Handler;

import com.google.gson.Gson;
import model.ChatMessage;
import model.Chatroom;
import model.Message;
import network.ClientHandler;
import network.ServerCore;

public class GetMessagesHandler implements MessageHandler {
    public void handle(Message msg, ClientHandler handler, ServerCore server) {
        Gson gson = new Gson();
        int roomId = msg.getRoomId();
        String userId = msg.getId();
        Chatroom room = server.getChatrooms().get(roomId);

        if (room != null) {
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
