package presenter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.Message;
import view.MainFrame;
import view.ProfileView;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Base64;
import javax.imageio.ImageIO;

public class ProfilePresenter {
    private final ProfileView view;
    private final String userId;
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private final MainFrame mainFrame;
    private final Gson gson = new Gson();

    public ProfilePresenter(ProfileView view, String userId, Socket socket, PrintWriter out, BufferedReader in, MainFrame mainFrame) {
        this.view = view;
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
        JOptionPane.showMessageDialog(null, "프로필 저장 요청이 전송되었습니다.");
    }

    public void selectProfileImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(null);
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

                view.setProfileImage(icon, imageBase64);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "이미지 불러오기 실패");
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

    public void applyUpdatedProfile(String nickname, String intro, String imageBase64) {
        mainFrame.updateGreeting(nickname);
        mainFrame.getFriendPanel().getPresenter().updateMyProfile();
    }
}
