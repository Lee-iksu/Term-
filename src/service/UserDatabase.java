package service;

import java.util.ArrayList;

import model.User;

public class UserDatabase {
    private static UserDatabase instance = new UserDatabase();
    private ArrayList<User> users;

    private UserDatabase() {
        users = new ArrayList<>();

        // 테스트 계정
        User test1 = new User("test1", "1234");
        test1.setNickname("테스트1");
        test1.setIntro("안녕하세요. 저는 test1입니다.");
        users.add(test1);

        User test2 = new User("test2", "1234");
        test2.setNickname("테스트2");
        test2.setIntro("안녕하세요. 저는 test2입니다.");
        users.add(test2);
        
        User defaultTest = new User("test", "1234");
        defaultTest.setNickname("기본계정");
        defaultTest.setIntro("기본 테스트 계정입니다.");
        users.add(defaultTest);
    }

    public static UserDatabase shared() {
        return instance;
    }

    public boolean isValidUser(String id, String password) {
        for (User user : users) {
            if (user.getId().equals(id) && user.verifyPassword(password)) {
                return true;
            }
        }
        return false;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public boolean isDuplicateId(String id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public User getUserById(String id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }
}
