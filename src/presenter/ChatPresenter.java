package presenter;

import java.awt.Component;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import Controller.MultiChatController;
import model.Message;
import view.ChatView;
import view.DatePickerDialog;

public class ChatPresenter {
    private ChatView view;
    private final MultiChatController controller;

    public ChatPresenter(ChatView view, MultiChatController controller) {
        this.view = view;
        this.controller = controller;
    }

    public void onSendButtonClicked(String text) {
        String content = text.trim();
        if (content.isEmpty()) return;

        Message msg = new Message();
        msg.setType("SEND_MSG");
        msg.setRoomId(view.getRoomId());
        msg.setSender(view.getUserId());
        msg.setContent(content);

        controller.send(msg);
        //view.appendMessage(view.getUserId(), content, true);
        view.clearInputField();
    }

    public void onPhotoSendButtonClicked() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String fileName = System.currentTimeMillis() + "_" + file.getName();

            try {
                byte[] imageBytes = Files.readAllBytes(file.toPath());
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);

                Message msg = new Message();
                msg.setType("PHOTO_UPLOAD");
                msg.setSender(view.getUserId());
                msg.setRoomId(view.getRoomId());
                msg.setContent(base64Image);
                msg.setMsg(fileName);

                controller.send(msg);
                view.appendMessage(view.getUserId(), "🖼️ 사진 전송: " + fileName, true);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "이미지 전송 실패: " + e.getMessage());
            }
        }
    }
    
    public void onCalendarButtonClicked() {
        SwingUtilities.invokeLater(() -> {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor((Component) view);
            DatePickerDialog dialog = new DatePickerDialog(parent);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                String date = dialog.getSelectedDate();
                String content = dialog.getScheduleText();

                if (!content.isEmpty()) {
                    String combined = "[" + date + "] " + content;

                    Message msg = new Message();
                    msg.setType("SCHEDULE_ADD");
                    msg.setRoomId(view.getRoomId()); 
                    msg.setId(view.getUserId());
                    msg.setContent(combined);

                    controller.send(msg); 
                    view.appendSystemMessage("📅 추가됨: " + combined); 
                } else {
                    view.appendSystemMessage("📅 선택한 날짜: " + date + " (내용 없음)");
                }
            }
        });
    }

    public void setView(ChatView view) {
        this.view = view;
    }

}
