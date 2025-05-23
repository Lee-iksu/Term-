// ServerMain.java (실행 진입점)
// ServerMain.java (실행 진입점)
package app;

import network.ServerCore;
import network.ServerUI;

public class ServerMain {
    public static void main(String[] args) {
        ServerUI ui = new ServerUI();
        ServerCore server = new ServerCore(ui);
        new Thread(server).start(); 
    }
}
