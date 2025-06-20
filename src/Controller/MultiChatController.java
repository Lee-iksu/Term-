package Controller;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import model.Message;
import model.ChatMessage;
import model.User;
import service.UserDatabase;
import view.MainFrame;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;

public class MultiChatController implements Runnable {
	private final String userId;
    private final Socket socket;
    private final PrintWriter outMsg;
    private final BufferedReader inMsg;


    private MainFrame mainFrame;
    private ChatRoomController chatRoomController;

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Gson gson = new Gson();
    private boolean status = true;
    private Thread thread;

    private final SimpleDateFormat time_sdf = new SimpleDateFormat("a hh:mm");
    private final SimpleDateFormat date_sdf = new SimpleDateFormat("yyyy년 MMM dd일 EEE요일", Locale.KOREA);
    private final Date date = new Date();

    public MultiChatController(String userId, Socket socket, PrintWriter outMsg, BufferedReader inMsg) {
        this.userId = userId;
        this.socket = socket;
        this.outMsg = outMsg;
        this.inMsg = inMsg;
    }

    private void sendLogoutAndClose(List<String> userList) {
        try {
            String logoutJson = gson.toJson(new Message(userId, "", "logout", "all", "", userList, 0));
            outMsg.println(logoutJson);
            outMsg.flush();
            Thread.sleep(300);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }


    public void connectServer() {
        try {
            logger.log(INFO, "[Client] 서버 연결 상태 OK (이미 생성자로 주입됨)");

            // 로그인 메시지 전송
            Message login = new Message(userId, "", "login", "all", "", null, 0);
            outMsg.println(gson.toJson(login));

            // 프로필 요청 전송
            Message profileRequest = new Message();
            profileRequest.setType("PROFILE_REQUEST");
            profileRequest.setId(userId);
            profileRequest.setRcvid(userId);
            outMsg.println(gson.toJson(profileRequest));

            // 수신 쓰레드 시작
            thread = new Thread(this);
            thread.start();
        } catch (Exception e) {
            logger.log(WARNING, "[MultiChatController] connectServer() 실패", e);
        }
    }


    public void send(Message msgObj) {
        if (outMsg != null) {
            String json = gson.toJson(msgObj);
            System.out.println("전송됨 (JSON): " + json);
            outMsg.println(json);
            outMsg.flush();
        } else {
            System.err.println("출력 스트림이 초기화되지 않았습니다.");
        }
    }

    public void send(String msg) {
        if (outMsg != null) {
            outMsg.println(msg);
            outMsg.flush();
            System.out.println("전송됨 (문자열): " + msg);
        }
    }

    int count = 0;

    @Override
    public void run() {
        String msg;
        status = true;
        Message m = null;

        while (status) {
            try {
                msg = inMsg.readLine();
                if (msg == null) break;

                System.out.println("[DEBUG] 수신된 메시지: " + msg);

                // 채팅 메시지 직접 파싱
                if (msg.contains("\"sender\"") && msg.contains("\"content\"") && msg.contains("\"roomId\"")) {
                    try {
                        JsonObject raw = JsonParser.parseString(msg).getAsJsonObject();
                        String sender = raw.get("sender").getAsString();
                        String content = raw.get("content").getAsString();
                        int roomId = raw.get("roomId").getAsInt();
                        String type = raw.has("type") ? raw.get("type").getAsString() : "";
                        boolean isHistorical = type.equals("HISTORY_MSG");

                        if (mainFrame != null && mainFrame.getChatPanel() != null &&
                            mainFrame.getChatPanel().getRoomId() == roomId) {

                            if (content.startsWith("[사진] ")) {
                                String fileName = content.substring(5).trim();
                                File imgFile = new File("photos", fileName);

                                SwingUtilities.invokeLater(() -> {
                                    if (imgFile.exists()) {
                                        ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
                                        Image scaled = icon.getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH);
                                        mainFrame.getChatPanel().appendImageMessage(sender, new ImageIcon(scaled));
                                    } else {
                                        mainFrame.getChatPanel().appendMessage(sender, "[사진] " + fileName + " (파일 없음)");
                                    }
                                });
                            } else if (isHistorical || !sender.equals(mainFrame.getUserId())) {
                                SwingUtilities.invokeLater(() -> {
                                    mainFrame.getChatPanel().appendMessage(sender, content);
                                });
                            }
                        }
                        continue;
                    } catch (Exception e) {
                        System.err.println("[ERROR] ChatMessage 파싱 실패");
                        e.printStackTrace();
                        continue;
                    }
                }

                // 일반 Message 처리
                m = gson.fromJson(msg, Message.class);

                // 접속자 목록 갱신
                List<String> receivedCheckList = m.getCheck();
                if (receivedCheckList != null && mainFrame != null && mainFrame.getFriendPanel() != null) {
                    SwingUtilities.invokeLater(() -> {
                        mainFrame.getFriendPanel().updateFriendList(receivedCheckList);
                    });
                }

                // 프로필 처리
                if ("PROFILE_RESPONSE".equals(m.getType())) {
                    String json = m.getProfile();
                    Map<String, String> profileData = gson.fromJson(json, Map.class);
                    String nickname = profileData.get("nickname");
                    String intro = profileData.get("intro");
                    String imageBase64 = profileData.get("image");
                    boolean isMe = m.getId().equals(mainFrame.getUserId());

                    if (isMe) {
                        User me = UserDatabase.shared().getUserById(mainFrame.getUserId());
                        if (me != null) {
                            me.setNickname(nickname);
                            me.setIntro(intro);
                            me.setImageBase64(imageBase64);
                        }
                    }

                    SwingUtilities.invokeLater(() -> {
                        mainFrame.getFriendPanel().displayUserInfo(nickname, intro, isMe, imageBase64);
                    });
                    continue;
                }

                // 채팅방 생성 응답
                if ("ROOM_CREATED".equals(m.getType())) {
                    int roomId = Integer.parseInt(m.getArgs()[0]);
                    String roomName = m.getArgs()[1];
                    String targetId = m.getArgs()[2];
                    if (chatRoomController != null)
                        chatRoomController.onRoomCreated(roomId, roomName, targetId);
                    continue;
                }

                // 사진 메시지
                if ("PHOTO_MSG".equals(m.getType())) {
                    String sender = m.getSender();
                    int roomId = m.getRoomId();
                    String fileName = m.getMsg();

                    if (mainFrame != null && mainFrame.getChatPanel() != null &&
                        mainFrame.getChatPanel().getRoomId() == roomId) {
                        SwingUtilities.invokeLater(() -> {
                            File imageFile = new File("photos", fileName);
                            if (imageFile.exists()) {
                                ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
                                Image scaled = icon.getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH);
                                mainFrame.getChatPanel().appendImageMessage(sender, new ImageIcon(scaled));
                            } else {
                                mainFrame.getChatPanel().appendMessage(sender, "[사진] " + fileName + " (파일 없음)");
                            }
                        });
                    }
                    continue;
                }

                // 일반 텍스트 메시지
                if ("SEND_MSG".equals(m.getType())) {
                    int roomId = m.getRoomId();
                    String sender = m.getSender();
                    String content = m.getContent();

                    if (mainFrame != null && mainFrame.getChatPanel() != null &&
                        mainFrame.getChatPanel().getRoomId() == roomId) {
                        SwingUtilities.invokeLater(() -> {
                            mainFrame.getChatPanel().appendMessage(sender, content);
                        });
                    }
                    continue;
                }

                // 서버 공지
                if ("server".equals(m.getType())) {
                    String log = "🟢 [알림] " + m.getId() + " " + m.getMsg();
                    SwingUtilities.invokeLater(() -> {
                        mainFrame.getChatPanel().appendSystemMessage(log);
                    });
                }

            } catch (IOException e) {
                logger.log(WARNING, "[MultiChatController] 수신 중 예외 발생");
                e.printStackTrace();
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
            logger.log(WARNING, "[MultiChatController] close() 에러 발생!!");
        }
    }

    public void setChatRoomController(ChatRoomController chatRoomController) {
        this.chatRoomController = chatRoomController;
    }

    public ChatRoomController getChatRoomController() {
        return chatRoomController;
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
}
