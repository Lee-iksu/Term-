package model;

import java.util.Date;

public class Schedule {
    private int roomId;
    private Date scheduleDate;
    private String content;
    private String creator;
    private Date timestamp;

    public Schedule() {
        this.timestamp = new Date(); // 생성 시점으로 기본값 설정
    }

    // Getter & Setter

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
