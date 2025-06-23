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

public class ClientHandler extends Thread { //서버 측, 클라이언트 1명을 전담 처리하는 스레드
    private Socket socket;             // 연결된 클라이언트 소켓
    private BufferedReader in;         // 입력 스트림 (수신)
    private PrintWriter out;           // 출력 스트림 (송신)
    private ServerCore server;         // 서버 코어 참조
    private String clientId;           // 클라이언트 ID
    private ChatRoomController chatRoomController; // UI 테스트용 잔재

    public ClientHandler(Socket socket, ServerCore server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String msg;
            Gson gson = new Gson();

            while ((msg = in.readLine()) != null) {
                try {
                    JsonElement element = JsonParser.parseString(msg);
                    if (element.isJsonObject()) {
                        // JSON -> Message 객체 변환
                        Message message = gson.fromJson(element, Message.class);

                        // Dispatcher에게 처리 위임
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
            // 연결 종료 → 클라이언트 제거
            server.removeClient(this);
        }
    }

    public void send(String msg) {
        if (out != null) out.println(msg); // 클라이언트로 전송
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
