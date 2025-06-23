package app; // 서버 실행

import network.ServerCore; 
import network.ServerUI;  

public class ServerMain {
    public static void main(String[] args) {
    	// 서버 로그 출력용 UI
        ServerUI ui = new ServerUI();
        ServerCore server = new ServerCore(ui);

        // 서버 스레드 시작 // 클라이언트 접속 받기 시작
        new Thread(server).start(); 
    }
}
