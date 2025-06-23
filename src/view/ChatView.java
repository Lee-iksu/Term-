// 채팅 화면의 View 역할을 정의하는 인터페이스
// View와 Presenter 간의 연결을 위한 명세 제공
package view.chat;

import javax.swing.ImageIcon;

public interface ChatView {

    // 일반 텍스트 메시지를 UI에 추가
	void appendMessage(String sender, String content, boolean isMine);

    // 이미지 메시지를 UI에 추가
    void appendImageMessage(String sender, ImageIcon imageIcon);

    // 시스템 알림 메시지를 UI에 추가
    void appendSystemMessage(String msg);

    // 현재 채팅방의 고유 ID 반환
    int getRoomId();

    // 입력 필드의 내용을 초기화
    void clearInputField();

    // 현재 사용자 ID 반환
    String getUserId();

    // 모든 메시지 영역 초기화
    void clearMessages();
}
