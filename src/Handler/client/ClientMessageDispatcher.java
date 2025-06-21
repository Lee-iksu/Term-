package Handler.client;

import java.util.HashMap;
import java.util.Map;

import model.Message;

public class ClientMessageDispatcher {
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
