package Handler.server;

import com.google.gson.Gson;
import java.util.HashSet;

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

	        Message forward = new Message();
	        forward.setType("SEND_MSG");
	        forward.setSender(msg.getSender());
	        forward.setRoomId(roomId);
	        forward.setContent(msg.getContent());

	        System.out.println("[DEBUG] room.getMembers(): " + room.getMembers());
	        
	        for (String member : new HashSet<>(room.getMembers())) {
	        	System.out.println("[SERVER] " + msg.getSender() + " → " + msg.getReceiver() + " : " + msg.getContent());
	            server.sendTo(member, gson.toJson(forward));
	        }
	    }
	}

}
