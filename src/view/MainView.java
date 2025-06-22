package view;

public interface MainView {
    void showFriendPanel();
    void showChatPanel();
    void showSchedulePanel();
    void showProfilePanel();
    void showChatRoom(String roomName, int roomId);
    void updateGreeting(String nickname);
}
