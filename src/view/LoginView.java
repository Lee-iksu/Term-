package view;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public interface LoginView {
    String getIdInput();
    String getPasswordInput();
    void showMessage(String msg);
    void moveToMainScreen(String id, Socket socket, PrintWriter out, BufferedReader in);
}
