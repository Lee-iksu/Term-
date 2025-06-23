package model; // 일정 정보(날짜, 내용 등)를 표현하는 모델 클래스

import java.util.Date;

public class Photo {
    private String fileName;
    private String path;
    private int roomId;
    private String sender;
    private Date timestamp;

    public Photo() {
        this.timestamp = new Date();
    }

    // Getter & Setter

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
