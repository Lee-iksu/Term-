package view.chat;

import javax.swing.ImageIcon;

public interface ChatView {
	void appendMessage(String sender, String content, boolean isMine);
    void appendImageMessage(String sender, ImageIcon imageIcon);
    void appendSystemMessage(String msg);
    int getRoomId();
    void clearInputField();
    String getUserId();
    void clearMessages();
}
