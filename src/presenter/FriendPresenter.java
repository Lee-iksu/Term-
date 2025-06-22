package presenter;

import java.util.ArrayList;
import java.util.List;

import Controller.ChatRoomController;
import model.User;
import service.UserDatabase;
import view.FriendView;

public class FriendPresenter {
    private final FriendView view;
    private final String userId;
    private ProfilePresenter profileController;
    private ChatRoomController chatRoomController;
    private String selectedFriendId;

    public FriendPresenter(FriendView view, String userId) {
        this.view = view;
        this.userId = userId;
    }

    public void onFriendSelected(String friendId) {
        if (!friendId.equals(userId)) {
            this.selectedFriendId = friendId; // 선택된 친구 저장
            view.enableChatButton(friendId);  // 버튼 활성화
        } else {
            this.selectedFriendId = null;     // 자기 자신 선택한 경우 초기화
            view.disableChatButton();
        }
    }

    public void onFriendDoubleClicked(String friendId) {
        User user = UserDatabase.shared().getUserById(friendId);
        if (user != null) {
            // 뷰 업데이트 (닉네임, 소개 등 표시)
            view.showUserProfile(user.getNickname(), user.getIntro(), user.getImageBase64(), friendId.equals(userId));

            // 실제 프로필 상세보기 열기
            if (profileController != null)
                profileController.showUserProfile(friendId);  // ✅ 이제 여기서 호출함
        }
    }

    public void loadFriendList(List<String> users) {
        view.showFriendList(users);
    }

    public void updateMyProfile() {
        User me = UserDatabase.shared().getUserById(userId);
        if (me != null)
            view.updateMyInfo(me.getNickname());
    }
    
    public void setProfileController(ProfilePresenter profileController) {
        this.profileController = profileController;
    }

	public void setChatRoomController(ChatRoomController controller) {
	    this.chatRoomController = controller;
	}
	
	public List<String> getFriendList() {
	    List<String> result = new ArrayList<>();
	    for (User user : UserDatabase.shared().getAllUsers()) {
	        if (!user.getId().equals(userId))
	            result.add(user.getId());
	    }
	    return result;
	}
	
	public void onStartChatButtonClicked() {
	    if (chatRoomController != null && selectedFriendId != null) {
	        chatRoomController.openChatRoomDialog(selectedFriendId);
	    } else {
	        System.err.println("[FriendPresenter] 채팅방 생성 실패: 컨트롤러 또는 대상 아이디가 null");
	    }
	}


}