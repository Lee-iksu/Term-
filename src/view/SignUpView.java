package view;

public interface SignUpView {
    String getIdInput();
    String getPasswordInput();
    String getPasswordCheckInput();

    void showMessage(String msg);
    void closeWindow();
}
