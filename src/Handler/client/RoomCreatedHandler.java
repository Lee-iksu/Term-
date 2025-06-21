package Handler.client;

import model.Message;
import Controller.MultiChatController;

public class RoomCreatedHandler implements MessageHandler {
    private final MultiChatController controller;

    public RoomCreatedHandler(MultiChatController controller) {
        this.controller = controller;
    }

    @Override
    public void handle(Message m) {
        int roomId = Integer.parseInt(m.getArgs()[0]);
        String name = m.getArgs()[1];
        String target = m.getArgs()[2];
        controller.getChatRoomController().onRoomCreated(roomId, name, target);
    }
}
