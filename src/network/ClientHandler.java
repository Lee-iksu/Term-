package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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
            Gson gson = new Gson();

            while ((msg = in.readLine()) != null) {
            	System.out.println("[DEBUG] 수신 원본 JSON: " + msg);
                try {
                    JsonElement element = JsonParser.parseString(msg);
                    if (element.isJsonObject()) {
                        Message message = gson.fromJson(element, Message.class);
                        MessageDispatcher.dispatch(message, this, server);
                    } else {
                        System.err.println("[ERROR] JSON 객체가 아님: " + msg);
                    }
                } catch (Exception e) {
                    System.err.println("[ERROR] 메시지 파싱 실패: " + msg);
                    e.printStackTrace();
                }
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
