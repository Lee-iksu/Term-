package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
