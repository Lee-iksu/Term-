package presenter;

import Handler.client.ChatMessageHandler;
import Handler.client.FriendListUpdateHandler;
import Handler.client.MessageDispatcher;
import Handler.client.PhotoMessageHandler;
import Handler.client.ProfileResponseHandler;
import Handler.client.RoomCreatedHandler;
import Handler.client.ServerNotificationHandler;
import model.Message;
import network.MessageClient;
import util.MessageParser;
import view.MainFrame;

public class ChatController implements Runnable {
    private final String userId;
    private final MessageClient client;
    private final MessageParser parser = new MessageParser();
    private final MessageDispatcher dispatcher = new MessageDispatcher();
    private MainFrame mainFrame;
    private volatile boolean running = true;

    public ChatController(String userId, MessageClient client, MainFrame frame) {
        this.userId = userId;
        this.client = client;
        this.mainFrame = frame;
        // 핸들러 등록
        dispatcher.register("SEND_MSG", new ChatMessageHandler(mainFrame));
        dispatcher.register("PROFILE_RESPONSE", new ProfileResponseHandler(frame.getFriendPanel()));
        dispatcher.register("ROOM_CREATED", new RoomCreatedHandler(this));
        dispatcher.register("PHOTO_MSG", new PhotoMessageHandler(frame.getChatPanel()));
        dispatcher.register("CHECK", new FriendListUpdateHandler(frame.getFriendPanel()));
        dispatcher.register("server", new ServerNotificationHandler(frame.getChatPanel()));
    }

    public void connectServer() {
        client.send(parser.toJson(new Message(userId, "", "login", "all", "", null, 0)));
        Message req = new Message(); req.setType("PROFILE_REQUEST"); req.setId(userId); req.setRcvid(userId);
        client.send(parser.toJson(req));
        new Thread(this).start();
    }

    public void send(Message m) {
        client.send(parser.toJson(m));
    }

    @Override
    public void run() {
        while (running) {
            try {
                String raw = client.receive(); if (raw==null) break;
                Message m = parser.parse(raw);
                dispatcher.dispatch(m);
            } catch (Exception e) {
                e.printStackTrace(); break;
            }
        }
        shutdown();
    }

    public void onRoomCreated(int roomId, String name, String target) {
        // UI 로직 or 추가 동작
        mainFrame.getChatRoomController().onRoomCreated(roomId, name, target);
    }

    public void shutdown() {
        running = false;
        try { client.close(); } catch (Exception ignored) {}
    }
}
