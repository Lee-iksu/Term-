package Handler.client;

import model.Message;
import view.ChatPanel;
import javax.swing.SwingUtilities;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;

public class PhotoMessageHandler implements MessageHandler {
    private final ChatPanel panel;

    public PhotoMessageHandler(ChatPanel panel) {
        this.panel = panel;
    }

    @Override
    public void handle(Message m) {
        SwingUtilities.invokeLater(() -> {
            File f = new File("photos", m.getMsg());
            if (f.exists()) {
                ImageIcon icon = new ImageIcon(f.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(160,160,Image.SCALE_SMOOTH);
                panel.appendImageMessage(m.getSender(), new ImageIcon(scaled));
            } else {
                panel.appendMessage(m.getSender(), "[사진] " + m.getMsg() + " (파일 없음)");
            }
        });
    }
}