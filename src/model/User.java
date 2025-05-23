package model;

import util.PasswordEncryption;

public class User {
    private String id;
    private String password; // 암호화된 비밀번호 저장
    private String nickname;
    private String intro;
    private String imageBase64;

    // 추가 필드 (보안/상태 관리)
    private String name;
    private int failCount;
    private boolean blocked;
    private boolean online;

    public User() {
        this.nickname = "사용자";
        this.intro = "";
        this.imageBase64 = "";
        this.failCount = 0;
        this.blocked = false;
        this.online = false;
    }

    public User(String id, String password) {
        this.id = id;
        this.password = PasswordEncryption.encrypt(password);
        this.nickname = id != null ? id : "사용자";
        this.intro = "";
        this.imageBase64 = "";
        this.failCount = 0;
        this.blocked = false;
        this.online = false;
    }

    // ===== 기본 정보 =====

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = PasswordEncryption.encrypt(password);
    }

    public String getNickname() {
        return nickname != null ? nickname : id;
    }

    public void setNickname(String nickname) {
        this.nickname = (nickname != null && !nickname.trim().isEmpty()) ? nickname : this.id;
    }

    public String getIntro() {
        return intro != null ? intro : "";
    }

    public void setIntro(String intro) {
        this.intro = (intro != null) ? intro : "";
    }

    public String getImageBase64() {
        return imageBase64 != null ? imageBase64 : "";
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64 != null ? imageBase64 : "";
    }

    // ===== 인증 관련 추가 필드 =====

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    // ===== 인증 =====

    public boolean verifyPassword(String inputPassword) {
        String decryptedPassword = PasswordEncryption.decrypt(this.password);
        return decryptedPassword != null && decryptedPassword.equals(inputPassword);
    }
}
