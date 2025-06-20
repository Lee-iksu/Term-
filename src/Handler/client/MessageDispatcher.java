package Handler.client;

import model.Message;
import java.util.HashMap;
import java.util.Map;

public class MessageDispatcher {
    private final Map<String, MessageHandler> handlers = new HashMap<>();

    public void register(String type, MessageHandler handler) {
        handlers.put(type, handler);
    }

    public void dispatch(Message message) {
        handlers.getOrDefault(message.getType(), msg -> {
            System.err.println("Unknown type: " + msg.getType());
        }).handle(message);
    }
}
