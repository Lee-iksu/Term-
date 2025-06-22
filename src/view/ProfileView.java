package view;

import javax.swing.ImageIcon;

public interface ProfileView {
    void setProfileImage(ImageIcon imageIcon, String imageBase64);
    void displayUserProfile(String nickname, String intro, String imageBase64);
}
