package Handler;

import model.Message;
import network.ClientHandler;
import network.ServerCore;

public interface MessageHandler {
    void handle(Message msg, ClientHandler handler, ServerCore server);
}
