package Controller;

import javax.swing.*;

import model.User;
import service.UserDatabase;

import java.awt.event.*;

public class SignUpController implements ActionListener {
    private JTextField idField;
    private JPasswordField pwField;

    public SignUpController(JTextField idField, JPasswordField pwField) {
        this.idField = idField;
        this.pwField = pwField;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String id = idField.getText().trim();
        String pw = new String(pwField.getPassword()).trim();

        if (id.isEmpty() || pw.isEmpty()) {
            JOptionPane.showMessageDialog(null, "ID와 비밀번호를 모두 입력하세요.");
            return;
        }

        UserDatabase db = UserDatabase.shared();

        if (db.isDuplicateId(id)) {
            JOptionPane.showMessageDialog(null, "이미 존재하는 ID입니다.");
            return;
        }

        User newUser = new User(id, pw); // 자동 암호화 포함

        if (db.registerUser(newUser)) {
            JOptionPane.showMessageDialog(null, "회원가입 완료! DB에 암호화된 정보가 저장되었습니다.");
        } else {
            JOptionPane.showMessageDialog(null, "회원가입 실패: DB 저장 중 오류가 발생했습니다.");
        }
    }
}
