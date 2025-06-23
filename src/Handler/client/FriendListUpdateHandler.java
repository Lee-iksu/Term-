package Handler.client;

import java.util.List;

import model.Message;
import presenter.FriendPresenter;
import view.friend.FriendPanel;

public class FriendListUpdateHandler implements MessageHandler {
    // 친구 목록 갱신 핸들러
    // CHECK 타입 메시지 수신 시 호출됨

    private final FriendPresenter presenter; // 프레젠터 참조 → 뷰 업데이트 목적

    public FriendListUpdateHandler(FriendPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void handle(Message m) {
        // 메시지 안에 check 리스트 포함됨
        List<String> list = m.getCheck();

        if (list == null) {
            // 데이터 없음 → 에러 출력 후 종료
            System.err.println("FriendListUpdateHandler: check 리스트가 null입니다.");
            return;
        }

        // presenter 통해 뷰 갱신 요청
        presenter.loadFriendList(list);
    }
}