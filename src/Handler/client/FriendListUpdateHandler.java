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
        SwingUtilities.invokeLater(() -> panel.updateFriendList(list));
    }
}
