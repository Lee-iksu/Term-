package service;

public class LoginService {
    public boolean authenticate(String id, String pw) {
        return service.UserDatabase.shared().isValidUser(id, pw);
    }
}
