package Controller;

import javax.swing.*;

import view.main.MainFrame;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AppLauncher {
    public static void launch(String id, Socket socket, PrintWriter out, BufferedReader in) {
        SwingUtilities.invokeLater(() -> {
            // 컨트롤러 생성  
            MultiChatController controller = new MultiChatController(id, socket, out, in);
            
            // 메인 프레임 생성  
            MainFrame frame = new MainFrame(id, socket, out, in, controller);

            // 뷰 <-> 컨트롤러 연결  
            controller.setMainFrame(frame);

            // 서버 수신 대기 시작,listen 쓰레드 따로 돌림  
            controller.connectServer();
        });
    }
}
