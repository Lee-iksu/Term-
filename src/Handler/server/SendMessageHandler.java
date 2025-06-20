package Handler.server;

import com.google.gson.Gson;

import model.ChatMessage;
import model.Chatroom;
import model.Message;
import network.ClientHandler;
import network.ServerCore;

import java.util.Date;

public class SendMessageHandler implements MessageHandler {
    public void handle(Message msg, ClientHandler handler, ServerCore server) {
        Gson gson = new Gson();
        int roomId = msg.getRoomId();
        Chatroom room = server.getChatrooms().get(roomId);
        if (room != null) {
            ChatMessage chatMsg = new ChatMessage(
                msg.getSender(), msg.getContent(), new Date(), roomId
            );
            room.addMessage(chatMsg);

            for (String member : room.getMembers()) {
                server.sendTo(member, gson.toJson(chatMsg));
            }
        }
    }
}
