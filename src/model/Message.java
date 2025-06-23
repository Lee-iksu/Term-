package model; // 클라이언트-서버 간 주고받는 메시지 데이터 구조

import java.util.Date;
import java.util.List;

public class Message {
    // 기본 필드
    private String id;
    private String msg;
    private String type;
    private String rcvid;
    private List<String> check;
    private int people;
    private String profile;
    private String sender;
    private String receiver;
    private int roomId;
    private Date timestamp;
    private String content; // content 필드로 args 처리

    // 생성자
    public Message() {
        this.timestamp = new Date();
    }

    public Message(String id, String msg, String type, String rcvid, List<String> check, int people) {
        this.id = id;
        this.msg = msg;
        this.type = type;
        this.rcvid = rcvid;
        this.check = check;
        this.people = people;
        this.timestamp = new Date();
    }

    public Message(String id, String msg, String type, String rcvid, String dummyTarget, List<String> check, int people) {
        this(id, msg, type, rcvid, check, people);
    }

    // Getter & Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRcvid() {
        return rcvid;
    }

    public void setRcvid(String rcvid) {
        this.rcvid = rcvid;
    }

    public List<String> getCheck() {
        return check;
    }

    public void setCheck(List<String> check) {
        this.check = check;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // args 처리를 위한 유틸
    public String[] getArgs() {
        return content != null ? content.split("\\|") : new String[0];
    }

    public void setArgs(String[] args) {
        if (args != null && args.length > 0) {
            this.content = String.join("|", args);
        }
    }
}
