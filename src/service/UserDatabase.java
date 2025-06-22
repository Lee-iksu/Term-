package service;

import model.User;
import util.PasswordEncryption;

import java.sql.*;
import java.util.ArrayList;

public class UserDatabase {
    private static final UserDatabase instance = new UserDatabase();
    private static final String DB_URL = "jdbc:sqlite:game.db";

    private UserDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = """
                CREATE TABLE IF NOT EXISTS user (
                    id TEXT PRIMARY KEY,
                    password TEXT NOT NULL,
                    nickname TEXT,
                    intro TEXT,
                    profile_image TEXT
                );
            """;
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            System.out.println("user 테이블 확인 또는 생성 완료");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static UserDatabase shared() {
        return instance;
    }

    public boolean registerUser(User user) {
        String sql = "INSERT INTO user (id, password, nickname, intro, profile_image) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getNickname());
            pstmt.setString(4, user.getIntro());
            pstmt.setString(5, user.getImageBase64());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
        	System.err.println("DB 저장 중 오류 발생");
            e.printStackTrace();
            return false;
        }
    }

    public boolean isValidUser(String id, String password) {
        String sql = "SELECT password FROM user WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String encrypted = rs.getString("password");
                String inputEncrypted = PasswordEncryption.encrypt(password);

                return inputEncrypted.equals(encrypted);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean isDuplicateId(String id) {
        String sql = "SELECT id FROM user WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // 이미 존재하면 true
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getUserById(String id) {
        String sql = "SELECT * FROM user WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("id"),
                        "", // password는 보안상 반환하지 않음
                        rs.getString("nickname"),
                        rs.getString("intro"),
                        rs.getString("profile_image")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateProfile(String id, String nickname, String intro, String imageBase64) {
        String sql = "UPDATE user SET nickname = ?, intro = ?, profile_image = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nickname);
            pstmt.setString(2, intro);
            pstmt.setString(3, imageBase64);
            pstmt.setString(4, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<User> getAllUsers() {
        ArrayList<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new User(
                        rs.getString("id"),
                        "",
                        rs.getString("nickname"),
                        rs.getString("intro"),
                        rs.getString("profile_image")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public void addUser(User user) {
        registerUser(user);
    }

    
}
