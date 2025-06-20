package Handler.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import model.Message;
import model.User;
import network.ClientHandler;
import network.ServerCore;
import service.UserDatabase;

public class ProfileRequestHandler implements MessageHandler {
    public void handle(Message msg, ClientHandler handler, ServerCore server) {
        Gson gson = new Gson();
        User u = UserDatabase.shared().getUserById(msg.getRcvid());

        Message res = new Message();
        res.setType("PROFILE_RESPONSE");
        res.setId(msg.getRcvid());
        res.setRcvid(msg.getId());

        JsonObject profile = new JsonObject();
        profile.addProperty("nickname", u.getNickname());
        profile.addProperty("intro", u.getIntro());
        profile.addProperty("image", u.getImageBase64() != null ? u.getImageBase64() : "");
        res.setProfile(profile.toString());

        server.sendTo(msg.getId(), gson.toJson(res));
    }
}
