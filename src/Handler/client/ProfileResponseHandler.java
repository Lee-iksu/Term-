package Handler.client;

import java.util.Map;

import javax.swing.SwingUtilities;

import com.google.gson.Gson;

import model.Message;
import model.User;
import service.UserDatabase;
import view.FriendPanel;

public class ProfileResponseHandler implements MessageHandler {
    private final FriendPanel panel;

    public ProfileResponseHandler(FriendPanel panel) {
        this.panel = panel;
    }

    @Override
    public void handle(Message m) {
        Map<String,String> data = new Gson().fromJson(m.getProfile(), Map.class);
        boolean isMe = m.getId().equals(panel.getUserId());

        if (isMe) {
            User me = UserDatabase.shared().getUserById(m.getId());
            if (me != null) {
                me.setNickname(data.get("nickname"));
                me.setIntro(data.get("intro"));
                me.setImageBase64(data.get("image"));
            }
        }

        SwingUtilities.invokeLater(() -> {
            panel.showUserProfile(
                data.get("nickname"),
                data.get("intro"),
                data.get("image"),
                isMe
            );
        });

    }
}
