package Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import network.MultiChatData;
import network.MultiChatUI;
import service.UserDatabase;
import view.MainFrame;

public class LoginController implements ActionListener {
    private JTextField idField;
    private JPasswordField pwField;

    public LoginController(JTextField idField, JPasswordField pwField) {
        this.idField = idField;
        this.pwField = pwField;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String id = idField.getText().trim();
        String pw = new String(pwField.getPassword());

        if (id.isEmpty() || pw.isEmpty()) {
            JOptionPane.showMessageDialog(null, "아이디와 비밀번호를 모두 입력하세요.");
            return;
        }

        if (!UserDatabase.shared().isValidUser(id, pw)) {
            JOptionPane.showMessageDialog(null, "로그인 실패: 아이디 또는 비밀번호가 틀렸습니다.");
            return;
        }

        try {
            Socket socket = new Socket("127.0.0.1", 12345);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);


            // 이후 화면 전환
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(idField);
            topFrame.dispose();

            SwingUtilities.invokeLater(() -> {
                MultiChatData chatData = new MultiChatData();
                MultiChatUI chatUI = new MultiChatUI(id);
                MultiChatController chatController = new MultiChatController(chatData, chatUI); // 아직 frame 없음

                // MainFrame에 controller 넘겨줌
                MainFrame frame = new MainFrame(id, socket, out, in, chatController);
                chatController.setMainFrame(frame); // frame을 다시 controller에 연결
                chatController.appMain(); // 로그인 및 수신 쓰레드 시작
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "서버 연결 실패: " + ex.getMessage());
        }
    }
}
