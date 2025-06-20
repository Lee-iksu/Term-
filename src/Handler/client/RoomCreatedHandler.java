package Handler.client;

import model.Message;
import presenter.ChatController;

public class RoomCreatedHandler implements MessageHandler {
    private final ChatController controller;

    public RoomCreatedHandler(ChatController controller) {
        this.controller = controller;
    }

    @Override
    public void handle(Message m) {
        int roomId = Integer.parseInt(m.getArgs()[0]);
        String name = m.getArgs()[1];
        String target = m.getArgs()[2];
        controller.onRoomCreated(roomId, name, target);
    }
}
