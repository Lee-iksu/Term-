package presenter;

import model.User;
import service.DAO.UserDatabase;
import view.signup.SignUpView;

public class SignUpPresenter {
    // 회원가입 화면과 연결되는 뷰 객체
    private SignUpView view;

    // 생성자: 뷰 객체를 주입받아 초기화
    public SignUpPresenter(SignUpView view) {
        this.view = view;
    }

    // 회원가입 버튼 클릭 시 호출되는 메서드
    public void onSignUpClicked() {
        // 사용자 입력값 수집
        String id = view.getIdInput();                  // ID 입력값
        String pw = view.getPasswordInput();            // 비밀번호 입력값
        String pwCheck = view.getPasswordCheckInput();  // 비밀번호 확인 입력값

        // 필수 항목 미입력 시 경고 메시지 출력
        if (id.isEmpty() || pw.isEmpty() || pwCheck.isEmpty()) {
            view.showMessage("모든 항목을 입력해주세요.");
            return;
        }

        // 비밀번호 불일치 확인
        if (!pw.equals(pwCheck)) {
            view.showMessage("비밀번호가 일치하지 않습니다.");
            return;
        }

        // 사용자 DB 접근을 위한 싱글턴 객체 호출
        UserDatabase db = UserDatabase.shared();

        // 중복 ID 검사
        if (db.isDuplicateId(id)) {
            view.showMessage("이미 존재하는 ID입니다.");
            return;
        }

        // 사용자 객체 생성 (내부적으로 암호화 처리 포함되어 있다고 가정)
        User newUser = new User(id, pw);

        // DB에 등록 시도
        if (db.registerUser(newUser)) {
            view.showMessage("회원가입 성공!");  // 성공 메시지
            view.closeWindow();                // 가입 창 닫기
        } else {
            view.showMessage("회원가입 실패: DB 저장 오류");  // 실패 메시지
        }
    }
}
