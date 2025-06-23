package Handler.client;

import java.util.Map;

import javax.swing.SwingUtilities;

import com.google.gson.Gson;

import model.Message;
import model.User;
import service.DAO.UserDatabase;
import view.friend.FriendPanel;

public class ProfileResponseHandler implements MessageHandler {
    // 프로필 정보 수신 시 → 뷰 갱신

    private final FriendPanel panel;

    public ProfileResponseHandler(FriendPanel panel) {
        this.panel = panel;
    }

    @Override
    public void handle(Message m) {
        // JSON -> Map 변환
        Map<String,String> data = new Gson().fromJson(m.getProfile(), Map.class);
        boolean isMe = m.getId().equals(panel.getUserId());

        // 본인인 경우 -> User 객체 정보 갱신
        if (isMe) {
            User me = UserDatabase.shared().getUserById(m.getId());
            if (me != null) {
                me.setNickname(data.get("nickname"));
                me.setIntro(data.get("intro"));
                me.setImageBase64(data.get("image"));
            }
        }

        // 프로필 패널에 정보 반영 (UI는 EDT에서)
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

