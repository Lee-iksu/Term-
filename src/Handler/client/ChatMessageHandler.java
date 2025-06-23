package Handler.client;

import model.Message;
import view.main.MainFrame;

import javax.swing.SwingUtilities;

public class ChatMessageHandler implements MessageHandler {
    // 채팅 메시지 수신 처리 핸들러
    // Dispatcher 에 등록됨 -> 메시지 들어오면 호출됨

    private final MainFrame mainFrame; // 전체 UI 프레임 참조

    public ChatMessageHandler(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public void handle(Message m) {
        // 채팅 메시지 도착 시 호출됨

        // 현재 열려있는 채팅방과 메시지의 roomId 일치하는지 확인
        if (mainFrame != null && mainFrame.getChatPanel() != null &&
            mainFrame.getChatPanel().getRoomId() == m.getRoomId()) {

            // UI 변경은 EDT에서 실행해야 함 → invokeLater 사용
            SwingUtilities.invokeLater(() -> {
                // 메시지 보낸 사람이 본인이면 오른쪽 정렬
                boolean isMine = m.getSender().equals(mainFrame.getChatPanel().getUserId());

                // 채팅창에 메시지 추가
                mainFrame.getChatPanel().appendMessage(m.getSender(), m.getContent(), isMine);
            });
        }
    }
}
