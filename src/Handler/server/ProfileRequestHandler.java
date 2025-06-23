package Handler.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import model.Message;
import model.User;
import network.ClientHandler;
import network.ServerCore;
import service.DAO.UserDatabase;

public class ProfileRequestHandler implements MessageHandler {
    // 클라이언트가 내 or 친구 프로필 요청 -> 서버가 응답 생성

    public void handle(Message msg, ClientHandler handler, ServerCore server) {
        Gson gson = new Gson();

        User u = UserDatabase.shared().getUserById(msg.getRcvid()); // 요청 대상 ID
        Message res = new Message();
        res.setType("PROFILE_RESPONSE");
        res.setId(msg.getRcvid());   // 요청 대상
        res.setRcvid(msg.getId());   // 요청 보낸 사람

        JsonObject profile = new JsonObject();
        profile.addProperty("nickname", u.getNickname());
        profile.addProperty("intro", u.getIntro());
        profile.addProperty("image", u.getImageBase64() != null ? u.getImageBase64() : "");
        res.setProfile(profile.toString());

        server.sendTo(msg.getId(), gson.toJson(res)); // 응답 전송
    }
}
