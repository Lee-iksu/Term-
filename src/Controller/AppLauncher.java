package Controller;

import Controller.MultiChatController;
import network.MultiChatData;
import network.MultiChatUI;
import view.MainFrame;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AppLauncher {
    public static void launch(String id, Socket socket, PrintWriter out, BufferedReader in) {
        SwingUtilities.invokeLater(() -> {
            MultiChatData chatData = new MultiChatData();
            MultiChatUI chatUI = new MultiChatUI(id);
            MultiChatController controller = new MultiChatController(chatData, chatUI);

            MainFrame frame = new MainFrame(id, socket, out, in, controller);
            controller.setMainFrame(frame);
            controller.appMain();
        });
    }
}
