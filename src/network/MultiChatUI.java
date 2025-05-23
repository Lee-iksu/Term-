package network;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.WindowListener;

public class MultiChatUI extends JFrame {
    private JPanel contentPane;

    protected JTextArea c_msgOut;
    protected JTextField msgInput;
    protected JButton sendButton;
    protected JButton deleteButton;
    protected JButton exitButton;
    protected JRadioButton secretRadio;
    protected DefaultListModel<String> nameOutModel;
    protected JList<String> nameOut;

    private String userId;  

    private final Color mint = new Color(102, 204, 204);

    public MultiChatUI(String userId) {
        super(userId + "님의 멀티챗방");
        this.userId = userId;
        initUI();
    }

    public String getUserId() {   
        return userId;
    }
    
    public JTextArea getMsgOutput() {
        return c_msgOut;
    }

    public JTextField getMsgInput() {
        return msgInput;
    }

    public JButton getSendButton() {
        return sendButton;
    }

    public DefaultListModel<String> getNameOutModel() {
        return nameOutModel;
    }

    public JList<String> getNameOut() {
        return nameOut;
    }
    
    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JButton getExitButton() {
        return exitButton;
    }

    public JRadioButton getSecretRadio() {
        return secretRadio;
    }


    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);
        setResizable(false);

        contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setBackground(Color.WHITE);
        setContentPane(contentPane);

        // Left Panel (Chat Area)
        JPanel chatPanel = new JPanel(new BorderLayout(5, 5));
        chatPanel.setBackground(Color.WHITE);

        JLabel chatLabel = new JLabel("채팅방");
        chatLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        chatLabel.setForeground(mint);
        chatPanel.add(chatLabel, BorderLayout.NORTH);

        c_msgOut = new JTextArea();
        c_msgOut.setFont(new Font("SansSerif", Font.PLAIN, 13));
        c_msgOut.setEditable(false);
        c_msgOut.setLineWrap(true);
        c_msgOut.setWrapStyleWord(true);
        c_msgOut.setBackground(new Color(245, 245, 245));

        JScrollPane chatScroll = new JScrollPane(c_msgOut);
        chatPanel.add(chatScroll, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        msgInput = new JTextField();
        msgInput.setFont(new Font("SansSerif", Font.PLAIN, 13));
        inputPanel.add(msgInput, BorderLayout.CENTER);
        msgInput.addActionListener(e -> sendButton.doClick());

        sendButton = new JButton("전송");
        stylizeButton(sendButton);
        inputPanel.add(sendButton, BorderLayout.EAST);

        chatPanel.add(inputPanel, BorderLayout.SOUTH);
        contentPane.add(chatPanel, BorderLayout.CENTER);

        // Right Panel (User List + Controls)
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(Color.WHITE);

        JLabel userLabel = new JLabel("접속자 목록");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        userLabel.setForeground(mint);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userPanel.add(userLabel);

        nameOutModel = new DefaultListModel<>();
        nameOut = new JList<>(nameOutModel);
        nameOut.setFont(new Font("SansSerif", Font.PLAIN, 13));
        nameOut.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        nameOut.setModel(nameOutModel);

        JScrollPane nameScroll = new JScrollPane(nameOut);
        nameScroll.setPreferredSize(new Dimension(150, 200));
        userPanel.add(Box.createVerticalStrut(10));
        userPanel.add(nameScroll);

        secretRadio = new JRadioButton("귓속말");
        secretRadio.setFont(new Font("SansSerif", Font.PLAIN, 13));
        secretRadio.setBackground(Color.WHITE);
        secretRadio.setAlignmentX(Component.CENTER_ALIGNMENT);
        userPanel.add(Box.createVerticalStrut(10));
        userPanel.add(secretRadio);

        deleteButton = new JButton("기록 삭제");
        stylizeButton(deleteButton);
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        userPanel.add(Box.createVerticalStrut(10));
        userPanel.add(deleteButton);

        exitButton = new JButton("종료");
        stylizeButton(exitButton);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        userPanel.add(Box.createVerticalStrut(10));
        userPanel.add(exitButton);

        contentPane.add(userPanel, BorderLayout.EAST);

        setVisible(true);
    }

    private void stylizeButton(JButton button) {
        button.setBackground(mint);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
    }

    public void addButtonActionListener(ActionListener listener) {
        sendButton.addActionListener(listener);
        deleteButton.addActionListener(listener);
        exitButton.addActionListener(listener);
    }

    public void addEnterKeyListener(KeyListener listener) {
        msgInput.addKeyListener(listener);
    }

    public void addButtonWindowListenr(WindowListener listener) {
        this.addWindowListener(listener);
    }
}
