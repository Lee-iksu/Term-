package view.profile;

import javax.swing.ImageIcon;

public interface ProfileView {

    // 선택된 프로필 이미지를 설정 (View 갱신 목적)
    void setProfileImage(ImageIcon imageIcon, String imageBase64);

    // 사용자 프로필 전체 정보 표시 (닉네임, 소개, 이미지)
    void displayUserProfile(String nickname, String intro, String imageBase64);
}
