package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageClient {
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;

    public MessageClient(Socket socket) throws IOException {
        this.socket = socket;
        this.out    = new PrintWriter(socket.getOutputStream(), true);
        this.in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void send(String json) {
        out.println(json);
    }

    public String receive() throws IOException {
        return in.readLine();
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
