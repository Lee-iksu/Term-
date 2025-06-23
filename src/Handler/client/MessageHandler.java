package Handler.client;

import model.Message;

//전략패턴
public interface MessageHandler {
    void handle(Message message); // 메시지 처리 동작 정의
}
