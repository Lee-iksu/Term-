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
import view.main.MainFrame;

public class MultiChatController implements Runnable { //전략패턴
    // 전체 통신 흐름 담당
    // 서버 연결 + 메시지 전송 + 수신 스레드
    // 뷰, 채팅방 컨트롤러 등과 연결됨

    private final String userId;           // 로그인한 사용자 ID
    private final Socket socket;           // 서버 연결 소켓
    private final PrintWriter outMsg;      // 서버로 보내는 스트림
    private final BufferedReader inMsg;    // 서버로부터 받는 스트림
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Gson gson = new Gson();  // JSON 변환용
    private final ClientMessageDispatcher dispatcher = new ClientMessageDispatcher(); // 핸들러 등록기

    private boolean status = true;         // 수신 루프 상태
    private Thread thread;                 // 수신 스레드

    private MainFrame mainFrame;           // 메인 프레임 참조
    private ChatRoomController chatRoomController; // 채팅방 컨트롤러 참조

    public MultiChatController(String userId, Socket socket, PrintWriter outMsg, BufferedReader inMsg) {
        // 기본 필드 초기화
        this.userId = userId;
        this.socket = socket;
        this.outMsg = outMsg;
        this.inMsg = inMsg;
    }

    public void setMainFrame(MainFrame mainFrame) {
        // 메인프레임 연결
        // 뷰 ↔ 컨트롤러 연결
        this.mainFrame = mainFrame;
        registerHandlers(); // 메시지 핸들러 등록
    }

    private void registerHandlers() {
        // 수신 타입별 핸들러 등록
        dispatcher.register("SEND_MSG", new ChatMessageHandler(mainFrame));
        dispatcher.register("PROFILE_RESPONSE", new ProfileResponseHandler(mainFrame.getFriendPanel()));
        dispatcher.register("ROOM_CREATED", new RoomCreatedHandler(this));
        dispatcher.register("PHOTO_MSG", new PhotoMessageHandler(mainFrame));
        dispatcher.register("CHECK", new FriendListUpdateHandler(mainFrame.getFriendPanel().getPresenter()));
        dispatcher.register("server", new ServerNotificationHandler(mainFrame.getChatPanel()));
        dispatcher.register("HISTORY_MSG", new ChatMessageHandler(mainFrame));
        dispatcher.register("SCHEDULE_ADD", new Handler.client.ScheduleAddHandler(mainFrame));
    }

    public void setChatRoomController(ChatRoomController chatRoomController) {
        this.chatRoomController = chatRoomController;
    }

    public ChatRoomController getChatRoomController() {
        return this.chatRoomController;
    }

    public void connectServer() {
        // 서버에 로그인 메시지 전송
        try {
            Message login = new Message(userId, "", "login", "all", "", null, 0);
            outMsg.println(gson.toJson(login));

            // 프로필 요청
            Message profileRequest = new Message();
            profileRequest.setType("PROFILE_REQUEST");
            profileRequest.setId(userId);
            profileRequest.setRcvid(userId);
            outMsg.println(gson.toJson(profileRequest));

            // 친구목록 상태 요청
            Message checkRequest = new Message();
            checkRequest.setType("CHECK");
            checkRequest.setId(userId);
            checkRequest.setRcvid(userId);
            System.out.println("[DEBUG] 보낸 CHECK 요청: " + gson.toJson(checkRequest));
            outMsg.println(gson.toJson(checkRequest));

            outMsg.flush(); // 버퍼 비우기

            // 수신 스레드 시작
            thread = new Thread(this);
            thread.start();

        } catch (Exception e) {
            logger.warning("[MultiChatController] connectServer() 실패: " + e.getMessage());
        }
    }

    public void send(Message msgObj) {
        // 서버로 메시지 전송
        if (outMsg != null) {
            String json = gson.toJson(msgObj); // 객체 → JSON 변환
            System.out.println("[DEBUG] controller.send() 전송 JSON: " + json);

            outMsg.println(json);
            outMsg.flush(); // 즉시 전송
        }
    }

    public void run() {
        // 수신 루프 시작
        status = true;
        while (status) {
            try {
                String raw = inMsg.readLine(); // 서버로부터 수신
                if (raw == null) break; // 연결 끊김

                Message msg = gson.fromJson(raw, Message.class); // JSON → 객체 변환
                dispatcher.dispatch(msg); // 타입에 맞는 핸들러 실행

            } catch (IOException e) {
                logger.warning("[MultiChatController] 수신 중 예외 발생: " + e.getMessage());
                close(); // 예외 발생 시 정리
            }
        }
        logger.info("[MultiChatController] 수신 스레드 종료됨");
    }

    private void close() {
        // 자원 정리
        try {
            if (socket != null && !socket.isClosed()) socket.close();
            if (inMsg != null) inMsg.close();
            if (outMsg != null) outMsg.close();
        } catch (IOException e) {
            logger.warning("[MultiChatController] close() 에러: " + e.getMessage());
        }
    }
}

