package presenter;

import model.User;
import service.DAO.UserDatabase;
import view.signup.SignUpView;

public class SignUpPresenter {
    private SignUpView view;

    public SignUpPresenter(SignUpView view) {
        this.view = view;
    }

    public void onSignUpClicked() {
        String id = view.getIdInput();
        String pw = view.getPasswordInput();
        String pwCheck = view.getPasswordCheckInput();

        if (id.isEmpty() || pw.isEmpty() || pwCheck.isEmpty()) {
            view.showMessage("모든 항목을 입력해주세요.");
            return;
        }

        if (!pw.equals(pwCheck)) {
            view.showMessage("비밀번호가 일치하지 않습니다.");
            return;
        }

        UserDatabase db = UserDatabase.shared();
        if (db.isDuplicateId(id)) {
            view.showMessage("이미 존재하는 ID입니다.");
            return;
        }

        User newUser = new User(id, pw); // 암호화 포함
        if (db.registerUser(newUser)) {
            view.showMessage("회원가입 성공!");
            view.closeWindow();
        } else {
            view.showMessage("회원가입 실패: DB 저장 오류");
        }
    }
}
