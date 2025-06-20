package Controller;

import view.MainFrame;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AppLauncher {
    public static void launch(String id, Socket socket, PrintWriter out, BufferedReader in) {
        SwingUtilities.invokeLater(() -> {
            // 컨트롤러는 서버 연결용
            MultiChatController controller = new MultiChatController(id, socket, out, in);
            
            // MainFrame은 controller를 받아서 생성
            MainFrame frame = new MainFrame(id, socket, out, in, controller);
            controller.setMainFrame(frame);

            // 서버 메시지 수신 스레드 실행
            controller.connectServer();
        });
    }
}
