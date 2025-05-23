// ServerCore.java (서버 실행 및 연결 관리)
package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ServerCore implements Runnable {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private List<String> userList = new ArrayList<>();
    private Logger logger = Logger.getLogger(ServerCore.class.getName());
    private ServerUI ui;

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
        for (ClientHandler c : clients) {
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
        model.Message update = new model.Message(
            "server", "", "", "update_userlist", "all", new ArrayList<>(userList), userList.size()
        );
        broadcast(new com.google.gson.Gson().toJson(update));
    }
}
