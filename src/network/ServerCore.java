// ServerCore.java (서버 실행 및 연결 관리)
package network;

import model.Chatroom;
import model.Message;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.Gson;


/**
 * 서버 실행 및 관리 중심 클래스
 * 
 * 클라이언트 연결 수락
 * ClientHandler 생성 후 리스트에 저장
 * 
 * 메시지 전송 방식:
 * - broadcast(): 전체에게 전송
 * - sendTo(): 특정 userId에게만 전송
 * 
 * 채팅방 목록(chatrooms), 그룹방 목록(groupChatRooms) 관리
 * - 참여자 확인, 생성 여부 확인 기능 포함
 * 
 * 접속자 목록(userList) 관리
 * - 로그인 시 추가, 종료 시 제거
 * - CHECK 메시지로 목록 갱신 전파
 * 
 * 로그 출력을 위해 ServerUI 참조
 */


public class ServerCore implements Runnable {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private List<String> userList = new ArrayList<>();
    private Logger logger = Logger.getLogger(ServerCore.class.getName());
    private ServerUI ui;
    
    private final Map<String, List<String>> groupChatRooms = new ConcurrentHashMap<>();

    
    private Map<Integer, Chatroom> chatrooms = new ConcurrentHashMap<>();

    public Map<Integer, Chatroom> getChatrooms() {
        return chatrooms;
    }
    public void createGroupChatRoom(String roomName, List<String> participantIds) {
        groupChatRooms.put(roomName, participantIds);
    }
    
    public ClientHandler getClientHandler(String userId) {
        for (ClientHandler c : clients) {
            if (userId.equals(c.getClientId())) {
                return c;
            }
        }
        return null;
    }

    public List<String> getGroupChatParticipants(String roomName) {
        return groupChatRooms.get(roomName);
    }

    public boolean roomExists(String roomName) {
        return groupChatRooms.containsKey(roomName);
    }

    public ServerCore(ServerUI ui) {
        this.ui = ui;
        this.ui.setActionListener(e -> {
            if (e.getSource() == ui.getStartButton()) {
                new Thread(this).start();
            } else if (e.getSource() == ui.getStopButton()) {
                System.exit(0);
            }
        });
    }
    public void sendAllChatroomsTo(String userId) {
        for (Chatroom room : chatrooms.values()) {
            if (room.getMembers().contains(userId)) {
                Message roomMsg = new Message();
                roomMsg.setType("ROOM_CREATED");
                roomMsg.setId("server");
                roomMsg.setRcvid(userId);
                roomMsg.setArgs(new String[] {
                    String.valueOf(room.getId()), 
                    room.getName(), 
                    getOtherMember(room.getMembers(), userId)
                });
                String json = new Gson().toJson(roomMsg);
                sendTo(userId, json);
            }
        }
    }

    private String getOtherMember(List<String> members, String self) {
        for (String m : members) {
            if (!m.equals(self)) return m;
        }
        return "";
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(12345);
            logger.info("[서버] 시작됨");
            ui.log("[서버] 시작됨");

            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, this);
                clients.add(handler);
                handler.start();
            }
        } catch (IOException e) {
            ui.log("[서버] 예외 발생: " + e.getMessage());
        }
    }

    public void broadcast(String message) {
        for (ClientHandler c : clients) {
            c.send(message);
        }
    }

    public void sendTo(String userId, String message) {
        // 리스트를 뒤에서부터 순회해서 가장 최신 연결 하나만 전송
        for (int i = clients.size() - 1; i >= 0; i--) {
            ClientHandler c = clients.get(i);
            if (userId.equals(c.getClientId())) {
                c.send(message);
                return;
            }
        }
    }


    public void removeClient(ClientHandler handler) {
        clients.remove(handler);
        userList.remove(handler.getClientId());
        updateUserListBroadcast();
    }

    public ServerUI getUI() {
        return ui;
    }

    public List<String> getUserList() {
        return userList;
    }

    public void updateUserListBroadcast() {
        Message update = new Message();
        update.setType("CHECK");                  
        update.setId("server");
        update.setCheck(new ArrayList<>(userList));

        String json = new Gson().toJson(update);
        broadcast(json);                          
    }

    public void removeClientById(String userId) {
        clients.removeIf(c -> userId.equals(c.getClientId()));
    }

}
