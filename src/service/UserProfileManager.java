package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class UserProfileManager {
    private static final String PROFILE_DIR = "profiles"; // 저장 경로 폴더

    static {
        File dir = new File(PROFILE_DIR);
        if (!dir.exists()) dir.mkdirs(); // 폴더 없으면 생성
    }

    public static void saveProfile(String userId, String nickname, String intro) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROFILE_DIR + "/" + userId + ".txt"))) {
            writer.write(nickname);
            writer.newLine();
            writer.write(intro);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] loadProfile(String userId) {
        File file = new File(PROFILE_DIR + "/" + userId + ".txt");
        if (!file.exists()) return new String[] { "", "" };

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String nickname = reader.readLine();
            String intro = reader.readLine();
            return new String[] { nickname != null ? nickname : "", intro != null ? intro : "" };
        } catch (IOException e) {
            e.printStackTrace();
            return new String[] { "", "" };
        }
    }
}
