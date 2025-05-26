package network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import model.Message;
import model.User;
import service.UserDatabase;

public class MessageDispatcher {
    public static void dispatch(Message msg, ClientHandler handler, ServerCore server) {
        System.out.println("[DEBUG] dispatch type: " + msg.getType());
        Gson gson = new Gson();
        ServerUI ui = server.getUI();

        switch (msg.getType()) {
            case "login":
                handler.setClientId(msg.getId());

                // DB에 존재하지 않으면 기본 유저 등록
                if (UserDatabase.shared().getUserById(msg.getId()) == null) {
                    User user = new User(msg.getId(), "");
                    user.setNickname(msg.getId());
                    user.setIntro("");
                    user.setImageBase64("");
                    UserDatabase.shared().registerUser(user);  // ✅ 변경됨
                }

                if (!server.getUserList().contains(msg.getId())) {
                    server.getUserList().add(msg.getId());
                }

                server.updateUserListBroadcast();
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

            case "PROFILE_SAVE":
                JsonObject prof = JsonParser.parseString(msg.getProfile()).getAsJsonObject();
                String nick = prof.get("nickname").getAsString();
                String intro = prof.get("intro").getAsString();
                String image = prof.has("image") ? prof.get("image").getAsString() : "";

                // DB 업데이트 처리
                UserDatabase.shared().updateProfile(msg.getId(), nick, intro, image);

                // 최신 데이터 반영 후 응답
                User updated = UserDatabase.shared().getUserById(msg.getId());
                Message reply = new Message();
                reply.setType("PROFILE_RESPONSE");
                reply.setId(msg.getId());
                reply.setRcvid(msg.getId());

                JsonObject refreshed = new JsonObject();
                refreshed.addProperty("nickname", updated.getNickname());
                refreshed.addProperty("intro", updated.getIntro());
                refreshed.addProperty("image", updated.getImageBase64());

                reply.setProfile(refreshed.toString());
                server.sendTo(msg.getId(), gson.toJson(reply));
                server.updateUserListBroadcast();
                break;

            case "CREATE_ROOM": {
                try {
                    String senderId = msg.getArgs()[0];
                    String targetId = msg.getArgs()[1];
                    String roomName = msg.getArgs()[2];

                    int roomId = (int) (System.currentTimeMillis() % 100000);

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

                    server.sendTo(senderId, gson.toJson(roomCreatedMsg1));
                    server.sendTo(targetId, gson.toJson(roomCreatedMsg2));

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
