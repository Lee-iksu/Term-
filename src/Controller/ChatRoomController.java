package Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Chatroom;
import model.Message;
import network.ClientCore;
import view.chat.ChatRoomListPanel;
import view.chat.ChatRoomSetupDialog;

public class ChatRoomController {
    // 채팅방 관리  

    private ClientCore controller; // 서버 통신 담당  
    private String userId; // 현재 사용자 id  
    private view.main.MainFrame mainFrame; // 메인 창 참조  
    private ChatRoomListPanel listPanel;   // 채팅방 목록 패널  
    private final Map<Integer, Chatroom> chatroomMap = new HashMap<>(); // roomId -> Chatroom 저장용  

    public ChatRoomController(ClientCore controller, String userId, view.main.MainFrame mainFrame) {
        this.controller = controller;
        this.userId = userId;
        this.mainFrame = mainFrame;
    }

    public void createGroupChatRoom(String roomName, List<String> selectedIds) {
        // 그룹 채팅방 생성 요청  
        Message msg = new Message();
        msg.setType("CREATE_GROUP_ROOM"); // 타입 지정  
        msg.setId(userId); // 방 만든 사람

        // 참여자 id들 조인  
        String joinedIds = String.join("|", selectedIds); 
        String msgContent = userId + "|" + roomName + "|" + joinedIds;
        msg.setMsg(msgContent); // 서버에서 파싱용

        controller.send(msg); // 서버로 전송
    }

    private String[] buildArgs(String roomName, List<String> ids) {
        // 서버에 넘길 args 조합  
        List<String> args = new ArrayList<>();
        args.add(userId);       // 본인  
        args.add(roomName);     // 방 이름  
        args.addAll(ids);       // 참여자 목록  
        return args.toArray(new String[0]);
    }

    public ClientCore getController() {
        return controller;
    }

    public void openChatRoomDialog(String targetId) {
        // 채팅방 생성 창 띄움  
        // 콜백으로 생성 함수 연결
        new ChatRoomSetupDialog(mainFrame, targetId, roomName -> {
            createChatRoomWithServer(roomName, targetId);
        }).setVisible(true);
    }

    public void setListPanel(ChatRoomListPanel panel) {
        // 외부에서 패널 연결
        this.listPanel = panel;
    }

    private void createChatRoomWithServer(String roomName, String targetId) {
        // 일반 채팅방 생성 요청 (1:1)  
        Message msg = new Message();
        msg.setType("CREATE_ROOM");
        msg.setId(userId);        // 생성자  
        msg.setRcvid(targetId);   // 상대방  
        msg.setArgs(new String[]{userId, targetId, roomName}); // 정보 전달

        controller.send(msg); 
    }

    public void onRoomCreated(int roomId, String roomName, String targetId) {
        // 방 생성 응답 처리  

        Chatroom room = new Chatroom();
        room.setId(roomId);
        room.setName(roomName);
        room.setGroup(false); // 1:1 기준  
        room.setMembers(List.of(userId, targetId)); // 참여자 저장  

        chatroomMap.put(roomId, room); // 맵에 등록

        if (listPanel != null) {
            // UI에 반영  
            listPanel.addChatRoom(roomName, roomId);
        } else {
            System.out.println("[ERROR] listPanel is null");
        }

        if (mainFrame != null) {
            // UI에서 해당 채팅방 보여줌  
            mainFrame.showChatRoom(roomName, roomId);
        }

        // 이전 채팅 불러오기 요청  
        Message getMsg = new Message();
        getMsg.setType("GET_MESSAGES");
        getMsg.setRoomId(roomId);
        getMsg.setId(userId);  
        controller.send(getMsg);  
    }

    public Chatroom getChatroomById(int roomId) {
        // 채팅방 객체 반환  
        return chatroomMap.get(roomId);
    }
}



