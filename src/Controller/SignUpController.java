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
        String id = idField.getText();
        String pw = new String(pwField.getPassword());

        if (id.isEmpty() || pw.isEmpty()) {
            JOptionPane.showMessageDialog(null, "ID와 비밀번호를 모두 입력하세요.");
            return;
        }
        
        UserDatabase db = UserDatabase.shared();

        if (db.isDuplicateId(id)) {
            JOptionPane.showMessageDialog(null, "이미 존재하는 ID입니다.");
            return;
        }
        
        // 비밀번호는 User 객체 생성 시 암호화됨
        UserDatabase.shared().addUser(new User(id, pw));
        JOptionPane.showMessageDialog(null, "회원가입 완료! 비밀번호가 암호화되어 저장되었습니다.");
    }
}