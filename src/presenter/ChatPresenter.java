package presenter;

import java.awt.Component;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import model.Message;
import network.ClientCore;
import view.chat.ChatView;
import view.schedule.DatePickerDialog;

public class ChatPresenter {
    // 사용자와 상호작용하는 UI 뷰 객체
    private ChatView view;

    // 서버 통신을 담당하는 클라이언트 핵심 로직 객체
    private final ClientCore controller;

    // 생성자: 뷰와 컨트롤러 객체를 주입받아 초기화
    public ChatPresenter(ChatView view, ClientCore controller) {
        this.view = view;
        this.controller = controller;
    }

    // 사용자가 텍스트 메시지를 입력하고 전송 버튼을 눌렀을 때 호출되는 메서드
    public void onSendButtonClicked(String text) {
        String content = text.trim();           // 입력된 텍스트의 앞뒤 공백 제거
        if (content.isEmpty()) return;          // 입력값이 비어있다면 아무 작업도 하지 않음

        // 메시지 전송을 위한 Message 객체 생성 및 데이터 설정
        Message msg = new Message();
        msg.setType("SEND_MSG");                // 메시지 타입을 일반 텍스트 메시지로 설정
        msg.setRoomId(view.getRoomId());        // 현재 채팅방 ID 설정
        msg.setSender(view.getUserId());        // 현재 사용자 ID 설정
        msg.setContent(content);                // 전송할 메시지 본문 설정

        controller.send(msg);                   // 컨트롤러를 통해 서버로 메시지를 전송
        view.clearInputField();                 // 입력 필드를 초기화하여 다음 메시지 입력 준비
    }

    // 사용자가 사진 전송 버튼을 눌렀을 때 호출되는 메서드
    public void onPhotoSendButtonClicked() {
        JFileChooser chooser = new JFileChooser();               // 파일 선택 다이얼로그 생성
        int result = chooser.showOpenDialog(null);               // 다이얼로그를 열어 사용자 입력 대기

        if (result == JFileChooser.APPROVE_OPTION) {             // 사용자가 파일을 선택한 경우
            File file = chooser.getSelectedFile();               // 선택된 파일 객체 획득
            String fileName = System.currentTimeMillis() + "_" + file.getName(); // 파일명 중복 방지를 위한 시간 접두어 추가

            try {
                byte[] imageBytes = Files.readAllBytes(file.toPath());           // 파일을 바이트 배열로 읽음
                String base64Image = Base64.getEncoder().encodeToString(imageBytes); // Base64 인코딩 처리

                // 사진 전송을 위한 메시지 객체 구성
                Message msg = new Message();
                msg.setType("PHOTO_UPLOAD");                    // 메시지 타입을 사진 업로드로 설정
                msg.setSender(view.getUserId());                // 현재 사용자 ID 설정
                msg.setRoomId(view.getRoomId());                // 현재 채팅방 ID 설정
                msg.setContent(base64Image);                    // 이미지 데이터를 Base64 문자열로 첨부
                msg.setMsg(fileName);                           // 파일명 메타정보로 추가

                controller.send(msg);                           // 컨트롤러를 통해 서버로 전송
                view.appendMessage(view.getUserId(), "🖼️ 사진 전송: " + fileName, true); // 사용자 화면에 전송 결과 표시

            } catch (Exception e) {
                // 파일 읽기 또는 전송 중 예외가 발생한 경우 사용자에게 알림
                JOptionPane.showMessageDialog(null, "이미지 전송 실패: " + e.getMessage());
            }
        }
    }

    // 사용자가 일정 추가 버튼을 눌렀을 때 호출되는 메서드
    public void onCalendarButtonClicked() {
        // UI 이벤트 처리를 위해 EDT(Event Dispatch Thread)에서 실행
        SwingUtilities.invokeLater(() -> {
            // 현재 뷰에서 가장 가까운 부모 JFrame을 탐색
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor((Component) view);

            // 사용자에게 날짜와 일정 내용을 입력받기 위한 다이얼로그 생성
            DatePickerDialog dialog = new DatePickerDialog(parent);
            dialog.setVisible(true);                               // 다이얼로그 표시 후 입력 대기

            if (dialog.isConfirmed()) {                            // 사용자가 '확인'을 눌렀을 경우
                String date = dialog.getSelectedDate();            // 선택된 날짜 정보
                String content = dialog.getScheduleText();         // 입력된 일정 내용

                if (!content.isEmpty()) {
                    String combined = "[" + date + "] " + content; // 날짜와 내용을 조합한 문자열 생성

                    // 일정 등록 메시지 구성
                    Message msg = new Message();
                    msg.setType("SCHEDULE_ADD");                   // 메시지 타입 설정
                    msg.setRoomId(view.getRoomId());               // 채팅방 ID
                    msg.setId(view.getUserId());                   // 사용자 ID
                    msg.setContent(combined);                      // 조합된 일정 내용

                    controller.send(msg);                          // 서버로 메시지 전송
                    view.appendSystemMessage("📅 추가됨: " + combined); // 시스템 메시지 형태로 사용자에게 표시
                } else {
                    // 내용이 비어 있는 경우 날짜 정보만 표시
                    view.appendSystemMessage("📅 선택한 날짜: " + date + " (내용 없음)");
                }
            }
        });
    }

    // 뷰 객체를 외부에서 재설정할 때 사용하는 메서드
    public void setView(ChatView view) {
        this.view = view;             // 뷰 객체를 교체
        view.clearMessages();         // 새 뷰의 메시지 목록 초기화
    }
}
