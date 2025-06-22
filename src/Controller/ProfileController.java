package Controller;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import model.Message;
import view.MainFrame;
import view.ProfilePanel;

public class ProfileController {
    private final String userId;
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private final MainFrame mainFrame;
    private final Gson gson = new Gson();

    public ProfileController(String userId, Socket socket, PrintWriter out, BufferedReader in, MainFrame mainFrame) {
        this.userId = userId;
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.mainFrame = mainFrame;
    }

    public void handleSaveProfile(String nickname, String intro, String imageBase64) {
        if (nickname.isEmpty()) {
            JOptionPane.showMessageDialog(null, "닉네임을 입력하세요.");
            return;
        }

        JsonObject profileObj = new JsonObject();
        profileObj.addProperty("nickname", nickname);
        profileObj.addProperty("intro", intro);
        profileObj.addProperty("image", imageBase64);

        Message msg = new Message();
        msg.setType("PROFILE_SAVE");
        msg.setId(userId);
        msg.setProfile(profileObj.toString());

        out.println(gson.toJson(msg));

        // 서버가 PROFILE_RESPONSE로 최신 상태 내려줌 → 거기서 화면 업데이트하면 됨
        JOptionPane.showMessageDialog(null, "프로필 저장 요청이 전송되었습니다.");
    }

    public void selectProfileImage(ProfilePanel panel) {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(panel);
        if (result == JFileChooser.APPROVE_OPTION) {
            File imgFile = chooser.getSelectedFile();
            try {
                BufferedImage img = ImageIO.read(imgFile);
                Image scaled = img.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(scaled);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(img, "png", baos);
                byte[] imageBytes = baos.toByteArray();
                String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);

                panel.setProfileImage(icon, imageBase64);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(panel, "이미지 불러오기 실패");
            }
        }
    }

    public void requestMyProfile() {
        Message request = new Message();
        request.setType("PROFILE_REQUEST");
        request.setId(userId);
        request.setRcvid(userId);
        out.println(gson.toJson(request));
    }

    public void showUserProfile(String targetId) {
        System.out.println("[클라이언트] PROFILE_REQUEST 전송: " + targetId);
        Message request = new Message();
        request.setType("PROFILE_REQUEST");
        request.setId(userId);
        request.setRcvid(targetId);
        out.println(gson.toJson(request));
        out.flush();
    }

    public void decodeAndDisplayImage(String imageBase64, JLabel photoLabel) {
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            try {
                byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
                ImageIcon icon = new ImageIcon(imageBytes);
                Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaled));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 서버에서 PROFILE_RESPONSE 받았을 때 호출되면 되는 메서드 예시
    public void applyUpdatedProfile(String nickname, String intro, String imageBase64) {
    	mainFrame.updateGreeting(nickname); 
        mainFrame.getFriendPanel().getPresenter().updateMyProfile();
        // 필요시 local 캐시에도 반영 가능
    }
}
