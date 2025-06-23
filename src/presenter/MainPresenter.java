package presenter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import model.Message;
import view.main.MainView;

public class MainPresenter {
    private final MainView view;
    private final String userId;
    private final PrintWriter out;
    private final Socket socket;

    public MainPresenter(MainView view, String userId, PrintWriter out, Socket socket) {
        this.view = view;
        this.userId = userId;
        this.out = out;
        this.socket = socket;
    }

    public void logout() {
        try {
            Gson gson = new Gson();
            Message logoutMsg = new Message(userId, "", "logout", "all", "", null, 0);
            out.println(gson.toJson(logoutMsg));
            out.flush();
            Thread.sleep(300);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println("로그아웃 메시지 전송 후 종료");
        }
    }

    public void onFriendTabClicked() {
        view.showFriendPanel();
    }

    public void onChatTabClicked() {
        view.showChatPanel();
    }

    public void onScheduleTabClicked() {
        view.showSchedulePanel();
    }

    public void onProfileTabClicked() {
        view.showProfilePanel();
    }
}