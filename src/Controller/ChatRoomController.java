package Controller;

import com.google.gson.Gson;
import model.Message;
import view.ChatRoomListPanel;
import view.ChatRoomSetupDialog;
import view.MainFrame;

import javax.swing.SwingUtilities;

public class ChatRoomController {
    private final MultiChatController controller;
    private final String userId;
    private final MainFrame mainFrame;

    public ChatRoomController(MultiChatController controller, String userId, MainFrame mainFrame) {
        this.controller = controller;
        this.userId = userId;
        this.mainFrame = mainFrame;
    }

    public void openChatRoomDialog(String targetId) {
        new ChatRoomSetupDialog(mainFrame, targetId, roomName -> {
            createChatRoomWithServer(roomName, targetId);
        }).setVisible(true);
    }

    private void createChatRoomWithServer(String roomName, String targetId) {
        Message msg = new Message();
        msg.setType("CREATE_ROOM");
        msg.setId(userId);
        msg.setRcvid(targetId);
        msg.setArgs(new String[]{userId, targetId, roomName});

        controller.send(msg); 
        System.out.println("[DEBUG] CREATE_ROOM 메시지 전송됨: " + roomName);
    }

    public void onRoomCreated(int roomId, String roomName, String targetId) {
        System.out.println("[DEBUG] onRoomCreated() 진입");

        ChatRoomListPanel listPanel = mainFrame.getChatRoomListPanel();
        System.out.println("[DEBUG] listPanel is null? " + (listPanel == null));

        if (listPanel != null) {
            SwingUtilities.invokeLater(() -> {
                listPanel.addRoom(roomName, roomId);
                System.out.println("[DEBUG] 채팅방 목록에 추가됨: " + roomName + " (" + roomId + ")");
            });
        } else {
            System.err.println("[ERROR] mainFrame.getChatRoomListPanel() == null");
        }
    }
}
