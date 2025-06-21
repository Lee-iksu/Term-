package Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

import com.google.gson.Gson;

import Handler.client.ChatMessageHandler;
import Handler.client.ClientMessageDispatcher;
import Handler.client.FriendListUpdateHandler;
import Handler.client.PhotoMessageHandler;
import Handler.client.ProfileResponseHandler;
import Handler.client.RoomCreatedHandler;
import Handler.client.ServerNotificationHandler;
import model.Message;
import view.MainFrame;

public class MultiChatController implements Runnable {
    private final String userId;
    private final Socket socket;
    private final PrintWriter outMsg;
    private final BufferedReader inMsg;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Gson gson = new Gson();
    private final ClientMessageDispatcher dispatcher = new ClientMessageDispatcher();
    private boolean status = true;
    private Thread thread;

    private MainFrame mainFrame;
    private ChatRoomController chatRoomController;

    public MultiChatController(String userId, Socket socket, PrintWriter outMsg, BufferedReader inMsg) {
        this.userId = userId;
        this.socket = socket;
        this.outMsg = outMsg;
        this.inMsg = inMsg;
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        registerHandlers();
    }

    private void registerHandlers() {
        dispatcher.register("SEND_MSG", new ChatMessageHandler(mainFrame));
        dispatcher.register("PROFILE_RESPONSE", new ProfileResponseHandler(mainFrame.getFriendPanel()));
        dispatcher.register("ROOM_CREATED", new RoomCreatedHandler(this));
        dispatcher.register("PHOTO_MSG", new PhotoMessageHandler(mainFrame));
        dispatcher.register("CHECK", new FriendListUpdateHandler(mainFrame.getFriendPanel()));
        dispatcher.register("server", new ServerNotificationHandler(mainFrame.getChatPanel()));       // 이미 있었을 것
        dispatcher.register("HISTORY_MSG", new ChatMessageHandler(mainFrame));
    
    
    }

    public void setChatRoomController(ChatRoomController chatRoomController) {
        this.chatRoomController = chatRoomController;
    }
    
    public ChatRoomController getChatRoomController() {
        return this.chatRoomController;
    }


    public void connectServer() {
        try {
        	
            Message login = new Message(userId, "", "login", "all", "", null, 0);
            outMsg.println(gson.toJson(login));

            Message profileRequest = new Message();
            profileRequest.setType("PROFILE_REQUEST");
            profileRequest.setId(userId);
            profileRequest.setRcvid(userId);
            outMsg.println(gson.toJson(profileRequest));
            
            Message checkRequest = new Message();
            checkRequest.setType("CHECK");
            checkRequest.setId(userId);
            checkRequest.setRcvid(userId);
            System.out.println("[DEBUG] 보낸 CHECK 요청: " + gson.toJson(checkRequest));
            
            outMsg.println(gson.toJson(checkRequest));

            outMsg.flush();

            thread = new Thread(this);
            thread.start();
        } catch (Exception e) {
            logger.warning("[MultiChatController] connectServer() 실패: " + e.getMessage());
        }
    }

    public void send(Message msgObj) {
        if (outMsg != null) {
            String json = gson.toJson(msgObj);
            System.out.println("[DEBUG] controller.send() 전송 JSON: " + json);

            outMsg.println(json);
            outMsg.flush();
        }
    }

    public void run() {
        status = true;
        while (status) {
            try {
                String raw = inMsg.readLine();
                if (raw == null) break;
                Message msg = gson.fromJson(raw, Message.class);
                dispatcher.dispatch(msg);
            } catch (IOException e) {
                logger.warning("[MultiChatController] 수신 중 예외 발생: " + e.getMessage());
                close();
            }
        }
        logger.info("[MultiChatController] 수신 스레드 종료됨");
    }

    private void close() {
        try {
            if (socket != null && !socket.isClosed()) socket.close();
            if (inMsg != null) inMsg.close();
            if (outMsg != null) outMsg.close();
        } catch (IOException e) {
            logger.warning("[MultiChatController] close() 에러: " + e.getMessage());
        }
    }
    
    
}
