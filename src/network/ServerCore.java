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


public class ServerCore implements Runnable {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private List<String> userList = new ArrayList<>();
    private Logger logger = Logger.getLogger(ServerCore.class.getName());
    private ServerUI ui;
 // ServerCore.java 내부
    private final Map<String, List<String>> groupChatRooms = new ConcurrentHashMap<>();

    
    private Map<Integer, Chatroom> chatrooms = new ConcurrentHashMap<>();

    public Map<Integer, Chatroom> getChatrooms() {
        return chatrooms;
    }
    public void createGroupChatRoom(String roomName, List<String> participantIds) {
        groupChatRooms.put(roomName, participantIds);
    }
 // ServerCore.java 안에 추가
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
