package view.friend;

import java.util.List;

public interface FriendView {

    // 친구 목록을 UI에 표시
    void showFriendList(List<String> users);

    // 특정 사용자(본인 또는 친구)의 프로필 정보 표시
    void showUserProfile(String nickname, String intro, String imageBase64, boolean isMine);

    // 내 정보(닉네임 등) 업데이트
    void updateMyInfo(String nickname);

    // 채팅 시작 버튼 활성화 (선택된 친구 기준)
    void enableChatButton(String friendId);

    // 채팅 시작 버튼 비활성화
    void disableChatButton();
}
