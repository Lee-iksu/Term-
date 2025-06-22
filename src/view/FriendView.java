package view;

import java.util.List;

public interface FriendView {
    void showFriendList(List<String> users);
    void showUserProfile(String nickname, String intro, String imageBase64, boolean isMine);
    void updateMyInfo(String nickname);
    void enableChatButton(String friendId);
    void disableChatButton();
}

