package Controller;

import javax.swing.*;

import model.User;
import service.DAO.UserDatabase;

import java.awt.event.*;

public class SignUpController implements ActionListener {
    // 회원가입 처리 담당
    // 입력값 확인 -> DB 중복체크 -> 등록 진행

    private JTextField idField;     // 사용자 ID 입력창
    private JPasswordField pwField; // 비밀번호 입력창

    public SignUpController(JTextField idField, JPasswordField pwField) {
        // 뷰에서 필드 연결 받아옴
        this.idField = idField;
        this.pwField = pwField;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 회원가입 버튼 눌렸을 때 호출됨

        String id = idField.getText().trim(); // 공백 제거
        String pw = new String(pwField.getPassword()).trim();

        // 빈 값 검사
        if (id.isEmpty() || pw.isEmpty()) {
            JOptionPane.showMessageDialog(null, "ID와 비밀번호를 모두 입력하세요.");
            return;
        }

        UserDatabase db = UserDatabase.shared(); // 싱글톤 DB 접근

        // ID 중복 검사
        if (db.isDuplicateId(id)) {
            JOptionPane.showMessageDialog(null, "이미 존재하는 ID입니다.");
            return;
        }

        // User 객체 생성 (내부에서 비밀번호 암호화 처리)
        User newUser = new User(id, pw); 

        // DB 등록 시도
        if (db.registerUser(newUser)) {
            JOptionPane.showMessageDialog(null, "회원가입 완료! DB에 암호화된 정보가 저장되었습니다.");
        } else {
            JOptionPane.showMessageDialog(null, "회원가입 실패: DB 저장 중 오류가 발생했습니다.");
        }
    }
}
