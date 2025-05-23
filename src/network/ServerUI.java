// ServerUI.java (서버 GUI)
package network;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerUI extends JFrame {
    private JTextArea logArea = new JTextArea();
    private JButton startButton = new JButton("서버 실행");
    private JButton stopButton = new JButton("서버 중지");
    private ActionListener listener;

    public ServerUI() {
        setTitle("Server UI");
        setSize(320, 360);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBounds(18, 15, 281, 256);
        add(scroll);

        logArea.setBackground(new Color(60, 63, 65));
        logArea.setForeground(Color.WHITE);
        logArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setEditable(false);
        log("[----서버 실행중----]");

        startButton.setBounds(28, 285, 115, 33);
        stopButton.setBounds(173, 285, 115, 33);
        add(startButton);
        add(stopButton);

        setVisible(true);
    }

    public void setActionListener(ActionListener listener) {
        this.listener = listener;
        startButton.addActionListener(listener);
        stopButton.addActionListener(listener);
    }

    public void log(String text) {
        logArea.append(text + "\n");
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getStopButton() {
        return stopButton;
    }
}
