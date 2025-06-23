package Handler.client;

import model.Message;
import view.chat.ChatPanel;

import javax.swing.SwingUtilities;

public class ServerNotificationHandler implements MessageHandler {
    // 서버 시스템 알림 메시지 처리
    // 채팅방 상단에 안내 메시지 띄움

    private final ChatPanel panel;

    public ServerNotificationHandler(ChatPanel panel) {
        this.panel = panel;
    }

    @Override
    public void handle(Message m) {
        String log = "[알림] " + m.getId() + " " + m.getMsg();

        // UI 작업 → EDT에서 실행
        SwingUtilities.invokeLater(() -> panel.appendSystemMessage(log));
    }
}
