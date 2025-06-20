package Handler;

import com.google.gson.Gson;
import model.ChatMessage;
import model.Chatroom;
import model.Message;
import network.ClientHandler;
import network.ServerCore;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.Date;

public class PhotoUploadHandler implements MessageHandler {
    public void handle(Message msg, ClientHandler handler, ServerCore server) {
        Gson gson = new Gson();
        try {
            String sender = msg.getSender();
            int roomId = msg.getRoomId();
            String fileName = msg.getMsg();
            String base64Data = msg.getContent();

            File photoDir = new File("photos");
            if (!photoDir.exists()) photoDir.mkdirs();

            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            File savedFile = new File(photoDir, fileName);
            try (FileOutputStream fos = new FileOutputStream(savedFile)) {
                fos.write(imageBytes);
            }

            Message photoMsg = new Message();
            photoMsg.setType("PHOTO_MSG");
            photoMsg.setSender(sender);
            photoMsg.setRoomId(roomId);
            photoMsg.setMsg(fileName);

            Chatroom room = server.getChatrooms().get(roomId);
            if (room != null) {
                for (String member : room.getMembers()) {
                    server.sendTo(member, gson.toJson(photoMsg));
                }
                ChatMessage photoChatMsg = new ChatMessage(
                    sender, "[사진] " + fileName, new Date(), roomId
                );
                room.addMessage(photoChatMsg);
            }

            server.getUI().log("[사진 저장 완료] " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[ERROR] PHOTO_UPLOAD 처리 실패");
        }
    }
}
