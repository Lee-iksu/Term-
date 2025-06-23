package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 클라이언트 측에서 서버에 소켓 연결을 생성하는 유틸 클래스
 * 
 * - Socket 객체 생성
 * - 입력/출력 스트림 반환
 * 
 */


public class ConnectionManager {
    public static Socket connect() throws Exception {
        return new Socket("127.0.0.1", 12345);
    }

    public static BufferedReader getReader(Socket socket) throws Exception {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static PrintWriter getWriter(Socket socket) throws Exception {
        return new PrintWriter(socket.getOutputStream(), true);
    }
}
