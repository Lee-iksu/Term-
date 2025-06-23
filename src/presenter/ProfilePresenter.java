package presenter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.Message;
import view.main.MainFrame;
import view.profile.ProfileView;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Base64;
import javax.imageio.ImageIO;

public class ProfilePresenter {
    // 사용자 프로필 화면을 담당하는 뷰 객체
    private final ProfileView view;

    // 현재 로그인한 사용자 ID
    private final String userId;

    // 서버와의 통신을 위한 소켓 및 스트림
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;

    // 메인 프레임: 전체 UI 업데이트 시 필요
    private final MainFrame mainFrame;

    // JSON 직렬화/역직렬화를 위한 Gson 인스턴스
    private final Gson gson = new Gson();

    // 생성자: 필드들을 초기화하며 뷰, 사용자 ID, 통신 객체 등을 주입받음
    public ProfilePresenter(ProfileView view, String userId, Socket socket, PrintWriter out, BufferedReader in, MainFrame mainFrame) {
        this.view = view;
        this.userId = userId;
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.mainFrame = mainFrame;
    }

    // 프로필 저장 버튼 클릭 시 호출되는 메서드
    public void handleSaveProfile(String nickname, String intro, String imageBase64) {
        if (nickname.isEmpty()) {
            // 닉네임은 필수 입력값이므로 비어 있으면 경고
            JOptionPane.showMessageDialog(null, "닉네임을 입력하세요.");
            return;
        }

        // 사용자 입력값을 JSON 객체로 구성
        JsonObject profileObj = new JsonObject();
        profileObj.addProperty("nickname", nickname);
        profileObj.addProperty("intro", intro);
        profileObj.addProperty("image", imageBase64);

        // 메시지 객체에 프로필 데이터 포함하여 전송 요청 생성
        Message msg = new Message();
        msg.setType("PROFILE_SAVE");
        msg.setId(userId);
        msg.setProfile(profileObj.toString());

        // 서버에 직렬화된 메시지를 전송
        out.println(gson.toJson(msg));

        // 사용자에게 저장 완료 메시지 출력
        JOptionPane.showMessageDialog(null, "프로필 저장 요청이 전송되었습니다.");
    }

    // 프로필 이미지 선택 시 호출되는 메서드
    public void selectProfileImage() {
        JFileChooser chooser = new JFileChooser();                 // 파일 선택 창 생성
        int result = chooser.showOpenDialog(null);                // 사용자 입력 대기

        if (result == JFileChooser.APPROVE_OPTION) {
            File imgFile = chooser.getSelectedFile();             // 선택된 이미지 파일

            try {
                BufferedImage img = ImageIO.read(imgFile);        // 이미지 파일 읽기
                Image scaled = img.getScaledInstance(120, 120, Image.SCALE_SMOOTH);  // 썸네일 크기로 조정
                ImageIcon icon = new ImageIcon(scaled);           // 아이콘으로 변환

                // 이미지 파일을 byte[] → Base64로 인코딩
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(img, "png", baos);
                byte[] imageBytes = baos.toByteArray();
                String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);

                // 뷰에 이미지 및 base64 문자열 전달
                view.setProfileImage(icon, imageBase64);

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "이미지 불러오기 실패");
            }
        }
    }

    // 현재 사용자의 프로필을 서버로부터 요청하는 메서드
    public void requestMyProfile() {
        Message request = new Message();
        request.setType("PROFILE_REQUEST");
        request.setId(userId);
        request.setRcvid(userId);                 // 본인 프로필 요청

        out.println(gson.toJson(request));
    }

    // 특정 사용자 ID에 대한 프로필 요청
    public void showUserProfile(String targetId) {
        System.out.println("[클라이언트] PROFILE_REQUEST 전송: " + targetId);

        Message request = new Message();
        request.setType("PROFILE_REQUEST");
        request.setId(userId);
        request.setRcvid(targetId);               // 대상 사용자 ID

        out.println(gson.toJson(request));
        out.flush();
    }

    // base64로 인코딩된 이미지를 디코딩하여 JLabel에 표시
    public void decodeAndDisplayImage(String imageBase64, JLabel photoLabel) {
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            try {
                byte[] imageBytes = Base64.getDecoder().decode(imageBase64);  // 디코딩
                ImageIcon icon = new ImageIcon(imageBytes);                   // 아이콘으로 변환
                Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaled));                   // 사진 라벨에 설정
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 프로필 정보가 변경되었을 때 UI에 반영하는 메서드
    public void applyUpdatedProfile(String nickname, String intro, String imageBase64) {
        mainFrame.updateGreeting(nickname);                               // 메인 화면 인사말 업데이트
        mainFrame.getFriendPanel().getPresenter().updateMyProfile();     // 친구 패널의 내 정보 갱신 요청
    }
}
