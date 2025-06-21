package Handler.client;

import model.Message;
import view.FriendPanel;
import javax.swing.SwingUtilities;
import java.util.List;

public class FriendListUpdateHandler implements MessageHandler {
    private final FriendPanel panel;

    public FriendListUpdateHandler(FriendPanel panel) {
        this.panel = panel;
    }

    @Override
    public void handle(Message m) {
        List<String> list = m.getCheck();
        if (list == null) {
            System.err.println("FriendListUpdateHandler: check 리스트가 null입니다.");
            return;
        }
        SwingUtilities.invokeLater(() -> panel.updateFriendList(list));
    }
}
