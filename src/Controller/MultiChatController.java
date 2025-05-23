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

import model.Message;
import model.User;
import network.MultiChatData;
import network.MultiChatUI;
import service.UserDatabase;
import view.MainFrame;

public class MultiChatController implements Runnable {
    private final MultiChatUI v;
    private final MultiChatData chatData;
    private MainFrame mainFrame;
    
    private ChatRoomController chatRoomController;

    private String ip = "127.0.0.1";
    private Socket socket;
    private BufferedReader inMsg = null;
    private PrintWriter outMsg = null;

    Gson gson = new Gson();
    Message m;
    boolean status;
    Logger logger;
    Thread thread;

    SimpleDateFormat time_sdf = new SimpleDateFormat("a hh:mm");
    Date date = new Date();
    SimpleDateFormat date_sdf = new SimpleDateFormat("yyyy년 MMM dd일 EEE요일", Locale.KOREA);

    DefaultListModel<String> check = new DefaultListModel<>();
    int people;

    public MultiChatController(MultiChatData chatData, MultiChatUI v, MainFrame mainFrame) {
        logger = Logger.getLogger(this.getClass().getName());
        this.chatData = chatData;
        this.v = v;
        this.mainFrame = mainFrame;
    }

    public MultiChatController(MultiChatData chatData, MultiChatUI v) {
        this(chatData, v, null);
    }

    public void appMain() {
        chatData.addObj(v.getMsgOutput());

        v.addButtonActionListener(e -> {
            Object obj = e.getSource();
            List<String> userList = Collections.list(v.getNameOutModel().elements());

            if (obj == v.getSendButton()) {
                if (v.getSecretRadio().isSelected()) {
                    String rcvid = v.getNameOut().getSelectedValue();
                    if (rcvid == null) {
                        JOptionPane.showMessageDialog(v.getContentPane(), "사람을 선택해주세요!");
                    } else {
                        JOptionPane.showMessageDialog(v.getContentPane(), rcvid + "님에게 귓속말을 보내시겠습니까?");
                        outMsg.println(gson.toJson(new Message(v.getUserId(), "", "secret", rcvid, "", userList, 0)));
                        outMsg.flush();
                        v.getMsgInput().setText("");
                    }
                } else {
                    outMsg.println(gson.toJson(new Message(v.getUserId(), v.getMsgInput().getText(), "message", "all", "", userList, 0)));
                    outMsg.flush();
                    v.getMsgInput().setText("");
                }
            } else if (obj == v.getExitButton()) {
                sendLogoutAndClose(userList);
            } else if (obj == v.getDeleteButton()) {
                outMsg.flush();
                v.getMsgOutput().setText("----------------------------------- 기록 삭제 ----------------------------------\n");
            }
        });

        v.addButtonWindowListenr(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent e) {
                List<String> userList = Collections.list(v.getNameOutModel().elements());
                sendLogoutAndClose(userList);
            }

            public void windowOpened(WindowEvent e) {}
            public void windowClosed(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
        });

        connectServer();
    }

    private void sendLogoutAndClose(List<String> userList) {
        try {
            String logoutJson = gson.toJson(new Message(v.getUserId(), "", "logout", "all", "", userList, 0));
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
            socket = new Socket(ip, 12345);
            logger.log(INFO, "[Client]Sever 연결 성공!!");

            inMsg = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outMsg = new PrintWriter(socket.getOutputStream(), true);

            List<String> userList = Collections.list(v.getNameOutModel().elements());

            m = new Message(v.getUserId(), "", "login", "all", "", userList, 0);
            outMsg.println(gson.toJson(m));

            Message profileRequest = new Message();
            profileRequest.setType("PROFILE_REQUEST");
            profileRequest.setId(v.getUserId());
            profileRequest.setRcvid(v.getUserId());
            outMsg.println(gson.toJson(profileRequest));

            thread = new Thread(this);
            thread.start();
        } catch (Exception e) {
            logger.log(WARNING, "[MultiChatUI]connectServer() Exception 발생!!");
            e.printStackTrace();
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

        if (count == 0) {
            chatData.refreshData("------------------------  " + date_sdf.format(date) + "  ------------------------\n");
            count++;
        }

        this.status = true;

        while (status) {
            try {
                msg = inMsg.readLine();
                System.out.println("[DEBUG] 수신된 원시 메시지: " + msg);
                m = gson.fromJson(msg, Message.class);

                Date date = new Date();
                List<String> receivedCheckList = m.getCheck();

                if ("PROFILE_RESPONSE".equals(m.getType())) {
                    String json = m.getProfile();
                    Map<String, String> profileData = gson.fromJson(json, Map.class);
                    String nickname = profileData.get("nickname");
                    String intro = profileData.get("intro");
                    String imageBase64 = profileData.get("image");

                    String targetId = m.getId();
                    boolean isMyself = m.getId().equals(mainFrame.getUserId());
                    
                    System.out.println("[DEBUG] nickname = " + nickname);
                    System.out.println("[DEBUG] intro = " + intro);
                    System.out.println("[DEBUG] imageBase64 length = " + (imageBase64 != null ? imageBase64.length() : "null"));

                    if (isMyself) {
                        User me = UserDatabase.shared().getUserById(mainFrame.getUserId());
                        if (me != null) {
                            me.setNickname(nickname);
                            me.setIntro(intro);
                            me.setImageBase64(imageBase64);

                            // 내 프로필 UI 갱신
                            SwingUtilities.invokeLater(() -> {
                                mainFrame.getFriendPanel().displayUserInfo(nickname, intro, true, imageBase64);
                            });
                        }
                    }


                    SwingUtilities.invokeLater(() -> {
                        if (mainFrame == null) {
                            System.out.println("[DEBUG] mainFrame is null");
                            return;
                        }

                        System.out.println("[DEBUG] PROFILE_RESPONSE 수신: nickname=" + nickname + ", isMyself=" + isMyself);
                        mainFrame.getFriendPanel().displayUserInfo(nickname, intro, isMyself, imageBase64);
                    });

                    continue;
                }
                
                else if ("ROOM_CREATED".equals(m.getType())) {
                    int roomId = Integer.parseInt(m.getArgs()[0]);
                    String roomName = m.getArgs()[1];
                    String targetId = m.getArgs()[2];

                    System.out.println("[DEBUG] ROOM_CREATED 수신: " + roomName + " (" + roomId + "), 대상: " + targetId); // 이거 추가

                    if (chatRoomController != null) {
                        chatRoomController.onRoomCreated(roomId, roomName, targetId);
                    } else {
                        System.err.println("[ERROR] chatRoomController가 null입니다.");
                    }
                    continue;
                }



                SwingUtilities.invokeLater(() -> {
                    if (mainFrame != null && mainFrame.getFriendPanel() != null) {
                        mainFrame.getFriendPanel().updateFriendList(receivedCheckList);
                    }
                });

                v.getNameOutModel().clear();
                for (String name : receivedCheckList) {
                    v.getNameOutModel().addElement(name);
                }
                people = m.getPeople();
                v.getNameOut().setModel(v.getNameOutModel());

                if ("server".equals(m.getType())) {
                    chatData.refreshData("🟢 [알림] " + m.getId() + " " + m.getMsg() + "\n");
                } else if ("s_secret".equals(m.getType())) {
                    chatData.refreshData(m.getId() + "→" + m.getRcvid() + " : " + m.getMsg() + "               " + time_sdf.format(date) + "\n");
                } else if ("message".equals(m.getType())) {
                    chatData.refreshData(m.getId() + " : " + m.getMsg() + "               " + time_sdf.format(date) + "\n");
                }

                v.getMsgOutput().setCaretPosition(v.getMsgOutput().getDocument().getLength());

            } catch (IOException e) {
                logger.log(WARNING, "[MultiChatUI]메시지 스트림 종료!!");
                e.printStackTrace();
                close();
            }
        }

        logger.info("[MultiChatUI]" + thread.getName() + " 메시지 수신 스레드 종료됨!!");
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
    
    public void setIO(Socket socket, PrintWriter out, BufferedReader in) {
        this.socket = socket;
        this.outMsg = out;
        this.inMsg = in;
    }
    
    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }


}