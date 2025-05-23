// ClientHandler.java (클라이언트별 스레드)
package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import Controller.ChatRoomController;
import model.Message;

public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ServerCore server;
    private String clientId;
    private ChatRoomController chatRoomController;

    public ClientHandler(Socket socket, ServerCore server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String msg;
            while ((msg = in.readLine()) != null) {
                Message message = new Gson().fromJson(msg, Message.class);
                MessageDispatcher.dispatch(message, this, server);
            }
        } catch (IOException e) {
            server.getUI().log("[클라이언트] 예외: " + e.getMessage());
        } finally {
            server.removeClient(this);
        }
    }

    public void send(String msg) {
        if (out != null) out.println(msg);
    }

    public void setClientId(String id) {
        this.clientId = id;
    }

    public String getClientId() {
        return clientId;
    }
    
    public ChatRoomController getChatRoomController() {
        return chatRoomController;
    }
    public void setChatRoomController(ChatRoomController chatRoomController) {
        this.chatRoomController = chatRoomController;
    }
}
