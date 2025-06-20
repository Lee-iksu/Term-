package Handler.client;

import model.Message;
import view.ChatPanel;
import javax.swing.SwingUtilities;

public class ServerNotificationHandler implements MessageHandler {
    private final ChatPanel panel;

    public ServerNotificationHandler(ChatPanel panel) {
        this.panel = panel;
    }

    @Override
    public void handle(Message m) {
        String log = "🟢 [알림] " + m.getId() + " " + m.getMsg();
        SwingUtilities.invokeLater(() -> panel.appendSystemMessage(log));
    }
}