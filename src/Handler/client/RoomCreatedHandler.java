package Handler.client;

import model.Message;
import network.ClientCore;

public class RoomCreatedHandler implements MessageHandler {
    // 채팅방 생성 응답 처리용 핸들러
    // 서버가 방 생성 후 보내주는 메시지 처리

    private final ClientCore controller;

    public RoomCreatedHandler(ClientCore controller) {
        this.controller = controller;
    }

    @Override
    public void handle(Message m) {
        // 메시지에 포함된 정보 파싱
        int roomId = Integer.parseInt(m.getArgs()[0]);
        String name = m.getArgs()[1];
        String target = m.getArgs()[2];

        // ChatRoomController에게 생성 알림 전달
        controller.getChatRoomController().onRoomCreated(roomId, name, target);
    }
}
