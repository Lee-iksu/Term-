package model; // 채팅방 내 개별 메시지(보낸이, 내용, 시간)를 표현

import java.util.Date;

public class ChatMessage {
    private String sender;
    private String content;
    private Date timestamp;
    private int roomId; 

    public ChatMessage(String sender, String content, Date timestamp) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    public ChatMessage(String sender, String content, Date timestamp, int roomId) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
        this.roomId = roomId;
    }

    // Getter & Setter
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
}
