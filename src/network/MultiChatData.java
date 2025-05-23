package network;
import javax.swing.JComponent;
import javax.swing.JTextArea;

public class MultiChatData {
    JTextArea msgOut;

    public void addObj(JComponent comp) {
        this.msgOut = (JTextArea)comp;
    }

  
    public void refreshData(String msg) {
        msgOut.append(msg);
    }
}