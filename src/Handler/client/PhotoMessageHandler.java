package Handler.client;

import model.Message;
import view.ChatPanel;
import view.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;

public class PhotoMessageHandler implements MessageHandler {
    private final MainFrame mainFrame;

    public PhotoMessageHandler(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public void handle(Message m) {
        SwingUtilities.invokeLater(() -> {
            ChatPanel panel = mainFrame.getChatPanel();

            if (panel == null) {
                System.err.println("[WARN] PhotoMessageHandler: panel이 null입니다 (방 아직 안 열림)");
                return;
            }

            if (panel.getRoomId() != m.getRoomId()) {
                System.out.println("[DEBUG] PHOTO_MSG 무시됨: 현재 채팅방(" + panel.getRoomId() + ") ≠ 메시지(" + m.getRoomId() + ")");
                return;
            }

            File f = new File("photos", m.getMsg());
            if (f.exists()) {
                ImageIcon icon = new ImageIcon(f.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH);
                panel.appendImageMessage(m.getSender(), new ImageIcon(scaled));
            } else {
                panel.appendMessage(m.getSender(), "[사진] " + m.getMsg() + " (파일 없음)");
            }
        });
    }
}
