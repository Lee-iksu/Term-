package presenter;

import java.util.ArrayList;
import java.util.List;

import Controller.ChatRoomController;
import model.User;
import service.DAO.UserDatabase;
import view.friend.FriendView;

public class FriendPresenter {
    // 친구 관련 UI를 담당하는 뷰 객체
    private final FriendView view;

    // 현재 로그인한 사용자 ID
    private final String userId;

    // 프로필 상세 정보를 보여주기 위한 ProfilePresenter (옵션)
    private ProfilePresenter profileController;

    // 채팅방을 열기 위한 ChatRoomController (옵션)
    private ChatRoomController chatRoomController;

    // 현재 선택된 친구의 ID
    private String selectedFriendId;

    // 생성자: 뷰와 현재 사용자 ID를 받아 초기화
    public FriendPresenter(FriendView view, String userId) {
        this.view = view;
        this.userId = userId;
    }

    // 사용자가 친구 목록에서 특정 친구를 선택했을 때 호출
    public void onFriendSelected(String friendId) {
        if (!friendId.equals(userId)) {
            this.selectedFriendId = friendId;        // 친구 ID 저장
            view.enableChatButton(friendId);         // 채팅 시작 버튼 활성화
        } else {
            this.selectedFriendId = null;            // 자기 자신을 선택한 경우 초기화
            view.disableChatButton();                // 채팅 시작 버튼 비활성화
        }
    }

    // 친구 항목을 더블 클릭했을 때 호출되는 메서드
    public void onFriendDoubleClicked(String friendId) {
        User user = UserDatabase.shared().getUserById(friendId); // 친구 ID로 유저 정보 조회

        if (user != null) {
            // 뷰에 프로필 정보 표시 (닉네임, 소개, 이미지 등)
            view.showUserProfile(user.getNickname(), user.getIntro(), user.getImageBase64(), friendId.equals(userId));

            // 프로필 컨트롤러가 연결되어 있다면 상세 프로필 보기 요청
            if (profileController != null)
                profileController.showUserProfile(friendId);
        }
    }

    // 사용자 목록을 전달받아 친구 목록으로 뷰에 출력
    public void loadFriendList(List<String> users) {
        view.showFriendList(users);
    }

    // 로그인한 본인의 프로필 정보를 가져와서 뷰에 표시
    public void updateMyProfile() {
        User me = UserDatabase.shared().getUserById(userId);
        if (me != null)
            view.updateMyInfo(me.getNickname());
    }

    // ProfilePresenter를 외부에서 주입
    public void setProfileController(ProfilePresenter profileController) {
        this.profileController = profileController;
    }

    // ChatRoomController를 외부에서 주입
    public void setChatRoomController(ChatRoomController controller) {
        this.chatRoomController = controller;
    }

    // 전체 사용자 중 본인을 제외한 ID 목록 반환
    public List<String> getFriendList() {
        List<String> result = new ArrayList<>();
        for (User user : UserDatabase.shared().getAllUsers()) {
            if (!user.getId().equals(userId))        // 본인을 제외
                result.add(user.getId());
        }
        return result;
    }

    // 채팅 시작 버튼 클릭 시 호출되는 메서드
    public void onStartChatButtonClicked() {
        // 컨트롤러와 대상 친구 ID가 모두 유효할 때만 채팅방 열기
        if (chatRoomController != null && selectedFriendId != null) {
            chatRoomController.openChatRoomDialog(selectedFriendId);
        } else {
            System.err.println("[FriendPresenter] 채팅방 생성 실패: 컨트롤러 또는 대상 아이디가 null");
        }
    }
}
