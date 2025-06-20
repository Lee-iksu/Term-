package Handler.client;

import model.Message;
import view.MainFrame;

import javax.swing.SwingUtilities;

public class ChatMessageHandler implements MessageHandler {
    private final MainFrame mainFrame;

    public ChatMessageHandler(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public void handle(Message m) {
        if (mainFrame != null && mainFrame.getChatPanel() != null &&
            mainFrame.getChatPanel().getRoomId() == m.getRoomId()) {
            SwingUtilities.invokeLater(() -> {
                mainFrame.getChatPanel().appendMessage(m.getSender(), m.getContent());
            });
        }
    }
}
