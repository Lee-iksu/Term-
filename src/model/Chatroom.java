package model; // 채팅방의 상태(참여자, 이름, 메시지 등)를 담는 모델

import java.util.ArrayList;
import java.util.List;

public class Chatroom {
    private int id;
    private String name;
    private boolean isGroup;
    private List<String> members;
    private List<ChatMessage> messages = new ArrayList<>();


    public Chatroom() {}

    // Getter & Setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
    
    public void addMessage(ChatMessage msg) {
        messages.add(msg);
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }
}
