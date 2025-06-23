package Handler.server;

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
    // 클라이언트가 보낸 base64 이미지 → 서버가 디코딩/저장 → 모든 참여자에게 알림 전송

    public void handle(Message msg, ClientHandler handler, ServerCore server) {
        try {
            String sender = msg.getSender();
            int roomId = msg.getRoomId();
            String fileName = msg.getMsg();
            String base64Data = msg.getContent();

            // 디렉토리 없으면 생성
            File photoDir = new File("photos");
            if (!photoDir.exists()) photoDir.mkdirs();

            // base64 → 이미지 디코딩 & 저장
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            File savedFile = new File(photoDir, fileName);
            try (FileOutputStream fos = new FileOutputStream(savedFile)) {
                fos.write(imageBytes);
            }

            // PHOTO_MSG 생성 후 참여자 전송
            Message photoMsg = new Message();
            photoMsg.setType("PHOTO_MSG");
            photoMsg.setSender(sender);
            photoMsg.setRoomId(roomId);
            photoMsg.setMsg(fileName);

            Chatroom room = server.getChatrooms().get(roomId);
            if (room != null) {
                for (String member : room.getMembers()) {
                    server.sendTo(member, new Gson().toJson(photoMsg));
                }

                // 채팅 기록에도 추가
                ChatMessage photoChatMsg = new ChatMessage(
                    sender, "[사진] " + fileName, new Date(), roomId
                );
                room.addMessage(photoChatMsg);
            }

            server.getUI().log("[사진 저장 완료] " + fileName);
        } catch (Exception e) {
            System.err.println("[ERROR] PHOTO_UPLOAD 처리 실패");
            e.printStackTrace();
        }
    }
}
