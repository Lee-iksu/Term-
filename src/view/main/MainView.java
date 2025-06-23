// 메인 화면(MainFrame)에서 Presenter가 호출할 View 동작 명세
// 화면 전환 및 UI 상태 갱신을 위한 인터페이스
package view.main;

public interface MainView {

    // 친구 목록 화면으로 전환
    void showFriendPanel();

    // 채팅방 목록 화면으로 전환
    void showChatPanel();

    // 일정 관리 화면으로 전환
    void showSchedulePanel();

    // 사용자 프로필 화면으로 전환
    void showProfilePanel();

    // 특정 채팅방 화면으로 전환 (방 이름, 방 ID 전달)
    void showChatRoom(String roomName, int roomId);

    // 상단 인사말 메시지를 갱신 (닉네임 기준)
    void updateGreeting(String nickname);
}
