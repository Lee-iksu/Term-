package util;

import com.google.gson.Gson;
import model.Message;

public class MessageParser {
    private final Gson gson = new Gson();

    public Message parse(String rawJson) {
        return gson.fromJson(rawJson, Message.class);
    }

    public String toJson(Message message) {
        return gson.toJson(message);
    }
}
