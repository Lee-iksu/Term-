package Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Chatroom;
import model.Message;
import view.ChatRoomListPanel;
import view.ChatRoomSetupDialog;

public class ChatRoomController {
    private MultiChatController controller;
    private String userId;
    private view.MainFrame mainFrame;
    private ChatRoomListPanel listPanel;
    private final Map<Integer, Chatroom> chatroomMap = new HashMap<>();


    public ChatRoomController(MultiChatController controller, String userId, view.MainFrame mainFrame) {
        this.controller = controller;
        this.userId = userId;
        this.mainFrame = mainFrame;
        System.out.println("[DEBUG] ChatRoomController 호출됨");
    }
    
    public void createGroupChatRoom(String roomName, List<String> selectedIds) {
        Message msg = new Message();
        msg.setType("CREATE_GROUP_ROOM");
        msg.setId(userId);  // 방 만드는 사람

        // 여기서 msg.setMsg(...) 반드시 호출
        String joinedIds = String.join("|", selectedIds);
        String msgContent = userId + "|" + roomName + "|" + joinedIds;
        msg.setMsg(msgContent);

        controller.send(msg);
        System.out.println("[DEBUG] CREATE_GROUP_ROOM 메시지 전송됨: " + roomName);
    }


    private String[] buildArgs(String roomName, List<String> ids) {
        List<String> args = new ArrayList<>();
        args.add(userId);        // 방 생성자 포함
        args.add(roomName);      // 방 이름
        args.addAll(ids);        // 참여자들
        return args.toArray(new String[0]);
    }


    public MultiChatController getController() {
        return controller;
    }

    public void openChatRoomDialog(String targetId) {
        new ChatRoomSetupDialog(mainFrame, targetId, roomName -> {
            createChatRoomWithServer(roomName, targetId);
        }).setVisible(true);
    }
    public void setListPanel(ChatRoomListPanel panel) {
        this.listPanel = panel;
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
        
        Chatroom room = new Chatroom();
        room.setId(roomId);
        room.setName(roomName);
        room.setGroup(false); // 단체 채팅은 아님
        room.setMembers(List.of(userId, targetId)); // 나 + 상대방

        chatroomMap.put(roomId, room); 

        if (listPanel != null) {
            System.out.println("[DEBUG] addChatRoom 호출: " + roomName + ", " + roomId);
            listPanel.addChatRoom(roomName, roomId); 
        } else {
            System.out.println("[ERROR] listPanel is null");
        }
        if (mainFrame != null) {
            mainFrame.showChatRoom(roomName, roomId);
        }
        
        Message getMsg = new Message();
        getMsg.setType("GET_MESSAGES");
        getMsg.setRoomId(roomId);
        getMsg.setId(userId);  
        controller.send(getMsg);  

    }
    
    public Chatroom getChatroomById(int roomId) {
        return chatroomMap.get(roomId);
    }


}
