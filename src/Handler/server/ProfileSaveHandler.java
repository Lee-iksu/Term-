package Handler.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import model.Message;
import model.User;
import network.ClientHandler;
import network.ServerCore;
import service.DAO.UserDatabase;

public class ProfileSaveHandler implements MessageHandler {
    // 프로필 저장 요청 처리 -> DB 저장 -> 다시 클라이언트에게 최신 상태 전송

    public void handle(Message msg, ClientHandler handler, ServerCore server) {
        Gson gson = new Gson();
        JsonObject prof = JsonParser.parseString(msg.getProfile()).getAsJsonObject();

        String nick = prof.get("nickname").getAsString();
        String intro = prof.get("intro").getAsString();
        String image = prof.has("image") ? prof.get("image").getAsString() : "";

        // DB 저장
        boolean saved = UserDatabase.shared().updateProfile(msg.getId(), nick, intro, image);
        System.out.println("[DEBUG] updateProfile 저장 성공 여부: " + saved);

        // 객체 상태도 동기화
        User usr = UserDatabase.shared().getUserById(msg.getId());
        if (usr != null) {
            usr.setNickname(nick);
            usr.setIntro(intro);
            usr.setImageBase64(image);
        }

        // 응답 메시지 구성
        Message reply = new Message();
        reply.setType("PROFILE_RESPONSE");
        reply.setId(msg.getId());
        reply.setRcvid(msg.getId());

        JsonObject refreshed = new JsonObject();
        refreshed.addProperty("nickname", usr.getNickname());
        refreshed.addProperty("intro", usr.getIntro());
        refreshed.addProperty("image", usr.getImageBase64());
        reply.setProfile(refreshed.toString());

        server.sendTo(msg.getId(), gson.toJson(reply));
        server.updateUserListBroadcast(); // 프로필 바뀐 사람 전체에게 반영
    }
}
