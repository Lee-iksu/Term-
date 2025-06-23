package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageClient { //클라이언트 측에서 서버와의 메시지 송수신을 담당하는 순수 통신 클래스
    private final Socket socket;             // 서버 소켓 연결
    private final PrintWriter out;           // 서버로 전송
    private final BufferedReader in;         // 서버로부터 수신

    public MessageClient(Socket socket) throws IOException {
        this.socket = socket;
        this.out    = new PrintWriter(socket.getOutputStream(), true);
        this.in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void send(String json) {
        out.println(json); // 서버로 JSON 전송
    }

    public String receive() throws IOException {
        return in.readLine(); // 서버로부터 수신
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close(); // 모든 자원 종료
    }
}
