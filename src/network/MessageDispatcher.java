package network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.Arrays;

import model.Chatroom;
import model.Message;
import model.User;
import service.UserDatabase;
import service.UserProfileManager;

import java.util.Arrays;
import model.ChatMessage;
import java.util.Date;

import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
public class MessageDispatcher {
    public static void dispatch(Message msg, ClientHandler handler, ServerCore server) {
        System.out.println("[DEBUG] dispatch type: " + msg.getType());
        Gson gson = new Gson();
        ServerUI ui = server.getUI();
        

        switch (msg.getType()) {
            case "login":
                handler.setClientId(msg.getId());
                if (UserDatabase.shared().getUserById(msg.getId()) == null) {
                    User user = new User(msg.getId(), "");
                    user.setNickname(msg.getId());
                    user.setIntro("");
                    UserDatabase.shared().addUser(user);
                }
                if (!server.getUserList().contains(msg.getId())) {
                    server.getUserList().add(msg.getId());
                }
                server.updateUserListBroadcast();
                server.sendAllChatroomsTo(msg.getId());
                ui.log("사용자 " + msg.getId() + " 로그인했습니다.");
                break;

            case "logout":
                server.removeClient(handler);
                ui.log("사용자 " + msg.getId() + " 로그아웃했습니다.");
                break;

            case "PROFILE_REQUEST":
                User u = UserDatabase.shared().getUserById(msg.getRcvid());
                Message res = new Message();
                res.setType("PROFILE_RESPONSE");
                res.setId(msg.getRcvid());
                res.setRcvid(msg.getId());
                JsonObject profile = new JsonObject();
                profile.addProperty("nickname", u.getNickname());
                profile.addProperty("intro", u.getIntro());
                profile.addProperty("image", u.getImageBase64() != null ? u.getImageBase64() : "");
                res.setProfile(profile.toString());
                server.sendTo(msg.getId(), gson.toJson(res));
                break;
                
            case "PHOTO_UPLOAD": {
                try {
                    String sender = msg.getSender();
                    int roomId = msg.getRoomId();
                    String fileName = msg.getMsg();         // 확장자 포함한 파일명
                    String base64Data = msg.getContent();   // 인코딩된 이미지 데이터

                    // 1. 디렉토리 준비
                    File photoDir = new File("photos");
                    if (!photoDir.exists()) {
                        photoDir.mkdirs();
                    }

                    // 2. 디코딩하여 저장
                    byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                    File savedFile = new File(photoDir, fileName);
                    try (FileOutputStream fos = new FileOutputStream(savedFile)) {
                        fos.write(imageBytes);
                    }

                    // 3. PHOTO_MSG로 클라이언트에 전송
                    Message photoMsg = new Message();
                    photoMsg.setType("PHOTO_MSG");
                    photoMsg.setSender(sender);
                    photoMsg.setRoomId(roomId);
                    photoMsg.setMsg(fileName);  // 파일 이름만 전송
                    

                    Chatroom room = server.getChatrooms().get(roomId);
                    if (room != null) {
                        for (String member : room.getMembers()) {
                            server.sendTo(member, gson.toJson(photoMsg));
                        }
                    }

                    server.getUI().log("[사진 저장 완료] " + fileName);
                    
                 // [4] PHOTO 메시지도 Chatroom에 저장
                    ChatMessage photoChatMsg = new ChatMessage(
                        sender,
                        "[사진] " + fileName,  // content는 표시용 텍스트로 저장
                        new Date(),
                        roomId
                    );
                    room.addMessage(photoChatMsg);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("[ERROR] PHOTO_UPLOAD 처리 실패");
                }
                break;
            }

            case "CREATE_GROUP_ROOM": {
                String msgStr = msg.getMsg();
                if (msgStr == null) {
                    handler.send("ERROR|메시지 내용이 없습니다.");
                    break;
                }
                String[] tokens = msgStr.split("\\|");
                if (tokens.length < 3) {
                    handler.send("ERROR|메시지 형식 오류");
                    break;
                }
                String roomName = tokens[1];
                List<String> participants = Arrays.asList(tokens[2].split(","));
                int roomId = (int)(System.currentTimeMillis() % 100000);

                // 방 이름 중복 방지
                if (server.roomExists(roomName)) {
                    handler.send("ERROR|이미 존재하는 방 이름입니다.");
                    break;
                }

                // 1. Chatroom 객체 생성 및 등록 (서버 메모리에 반드시 저장!)
                Chatroom room = new Chatroom();
                room.setId(roomId);
                room.setName(roomName);
                room.setGroup(true);
                room.setMembers(participants);
                server.getChatrooms().put(roomId, room);

                // 2. 그룹 방 목록용 관리 (선택)
                server.createGroupChatRoom(roomName, participants);

                // 3. 참가자들에게 방 생성 알림 전송
                for (String participantId : participants) {
                    ClientHandler targetHandler = server.getClientHandler(participantId);
                    if (targetHandler != null) {
                        Message roomCreatedMsg = new Message();
                        roomCreatedMsg.setType("ROOM_CREATED");
                        roomCreatedMsg.setId("server");
                        // 단체방은 세 번째 인자를 "GROUP" 으로 둡니다 (1:1은 상대 아이디)
                        roomCreatedMsg.setArgs(new String[]{
                            String.valueOf(roomId), roomName, "GROUP"
                        });
                        targetHandler.send(new Gson().toJson(roomCreatedMsg));
                    }
                }
                break;
            }



            case "PROFILE_SAVE":
                JsonObject prof = JsonParser.parseString(msg.getProfile()).getAsJsonObject();
                String nick = prof.get("nickname").getAsString();
                String intro = prof.get("intro").getAsString();
                String image = prof.has("image") ? prof.get("image").getAsString() : "";

                // ✅ 여기 추가
                boolean saved = UserDatabase.shared().updateProfile(msg.getId(), nick, intro, image);
                System.out.println("[DEBUG] updateProfile 저장 성공 여부: " + saved);

                User usr = UserDatabase.shared().getUserById(msg.getId());
                if (usr != null) {
                    usr.setNickname(nick);
                    usr.setIntro(intro);
                    usr.setImageBase64(image);
                }

                Message reply = new Message();
                reply.setType("PROFILE_RESPONSE");
                reply.setId(msg.getId());
                reply.setRcvid(msg.getId());
                JsonObject refreshed = new JsonObject();
                refreshed.addProperty("nickname", usr.getNickname());
                refreshed.addProperty("intro", usr.getIntro());
                refreshed.addProperty("image", usr.getImageBase64());
                reply.setProfile(refreshed.toString());
                server.sendTo(msg.getId(), gson.toJson(reply));
                server.updateUserListBroadcast();
                break;

            
            case "SEND_MSG": {
                int roomId = msg.getRoomId();
                Chatroom room = server.getChatrooms().get(roomId);
                if (room != null) {
                    ChatMessage chatMsg = new ChatMessage(
                        msg.getSender(),
                        msg.getContent(),
                        new Date(),     // ✅ 실제 Date 객체
                        roomId
                    );
                    room.addMessage(chatMsg);  // 🟢 서버 메모리에 저장

                    // 모든 멤버에게 메시지 전송
                    for (String member : room.getMembers()) {
                        server.sendTo(member, gson.toJson(chatMsg));
                    }
                }
                break;
            }

            case "GET_MESSAGES": {
                int roomId = msg.getRoomId();
                String userId = msg.getId();  // 누가 요청했는지 필요
                Chatroom room = server.getChatrooms().get(roomId);

                if (room != null) {
                	for (ChatMessage cm : room.getMessages()) {
                	    Message historyMsg = new Message();
                	    historyMsg.setType("HISTORY_MSG"); // ✅ 이게 맞습니다
                	    historyMsg.setSender(cm.getSender());
                	    historyMsg.setRoomId(roomId);
                	    historyMsg.setContent(cm.getContent());
                	    historyMsg.setTimestamp(cm.getTimestamp());
                	    server.sendTo(userId, gson.toJson(historyMsg)); // 요청한 유저에게만 전송
                	}

                }
                break;
            }







            case "CREATE_ROOM": {
                try {
                    String senderId = msg.getArgs()[0];
                    String targetId = msg.getArgs()[1];
                    String roomName = msg.getArgs()[2];

                    int roomId = (int) (System.currentTimeMillis() % 100000);

                    // 방 등록
                    Chatroom room = new Chatroom();
                    room.setId(roomId);
                    room.setName(roomName);
                    room.setGroup(false);
                    room.setMembers(Arrays.asList(senderId, targetId));
                    server.getChatrooms().put(roomId, room);  // 👈 저장
                    
                    Message roomCreatedMsg1 = new Message();
                    roomCreatedMsg1.setType("ROOM_CREATED");
                    roomCreatedMsg1.setId("server");
                    roomCreatedMsg1.setRcvid(senderId);
                    roomCreatedMsg1.setArgs(new String[]{String.valueOf(roomId), roomName, targetId});

                    Message roomCreatedMsg2 = new Message();
                    roomCreatedMsg2.setType("ROOM_CREATED");
                    roomCreatedMsg2.setId("server");
                    roomCreatedMsg2.setRcvid(targetId);
                    roomCreatedMsg2.setArgs(new String[]{String.valueOf(roomId), roomName, senderId});

                    String json1 = gson.toJson(roomCreatedMsg1);
                    String json2 = gson.toJson(roomCreatedMsg2);

                    System.out.println("[DEBUG] sendTo " + senderId + " → " + json1);
                    server.sendTo(senderId, json1);

                    System.out.println("[DEBUG] sendTo " + targetId + " → " + json2);
                    server.sendTo(targetId, json2);

                    System.out.println("[DEBUG] ROOM_CREATED 전송 완료: " + roomName + " (" + roomId + ")");
                } catch (Exception e) {
                    System.err.println("[ERROR] CREATE_ROOM 처리 중 예외 발생");
                    e.printStackTrace();
                }
                break;
            }



            default:
                server.broadcast(gson.toJson(msg));
                ui.log(msg.getId() + " : " + msg.getMsg());
        }
    }
}
