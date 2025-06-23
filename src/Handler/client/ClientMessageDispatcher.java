package Handler.client;

import java.util.HashMap;
import java.util.Map;

import model.Message;

public class ClientMessageDispatcher {
    // 메시지 타입별 핸들러 등록, 호출 담당
    // controller.run() 내부에서 사용됨

    private final Map<String, MessageHandler> handlers = new HashMap<>();
    // key: 메시지 타입 (ex: "SEND_MSG")
    // value: 해당 타입 처리할 핸들러 객체

    public void register(String type, MessageHandler handler) {
        // 핸들러 등록
        handlers.put(type, handler);
    }

    public void dispatch(Message message) {
        // 수신된 메시지 처리 요청
        // 타입에 해당하는 핸들러 찾아서 실행
        handlers
            .getOrDefault(message.getType(), msg -> {
                // 등록 안 된 타입이면 경고 출력
                System.err.println("Unknown type: " + msg.getType());
            })
            .handle(message); // 최종 실행
    }
}
