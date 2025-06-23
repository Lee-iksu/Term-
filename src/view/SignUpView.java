package view.signup;

public interface SignUpView {
    // 사용자로부터 입력받은 ID 값을 반환
    String getIdInput();

    // 사용자로부터 입력받은 비밀번호 값을 반환
    String getPasswordInput();

    // 사용자로부터 입력받은 비밀번호 확인 값을 반환
    String getPasswordCheckInput();

    // 메시지 출력 (ex. 오류 메시지, 성공 메시지)
    void showMessage(String msg);

    // 현재 View(창) 닫기
    void closeWindow();
}
