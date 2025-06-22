package Handler.client;

import java.util.List;

import model.Message;
import presenter.FriendPresenter;
import view.FriendPanel;

public class FriendListUpdateHandler implements MessageHandler {
    private final FriendPresenter presenter;

    public FriendListUpdateHandler(FriendPresenter presenter) {
    	this.presenter = presenter;
    }

    @Override
    public void handle(Message m) {
        List<String> list = m.getCheck();
        if (list == null) {
            System.err.println("FriendListUpdateHandler: check 리스트가 null입니다.");
            return;
        }
        presenter.loadFriendList(list);
    }
}
