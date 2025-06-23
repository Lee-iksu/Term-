// 일정 등록 시 사용하는 날짜 선택 다이얼로그
// 날짜와 일정 내용을 입력받아 확인 여부를 저장함
package view.schedule;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class DatePickerDialog extends JDialog {
    private JSpinner dateSpinner;            // 날짜 선택 스피너
    private JTextField scheduleField;        // 일정 내용 입력 필드
    private boolean confirmed = false;       // 확인 버튼 클릭 여부

    // 생성자: 부모 프레임 기준 모달 다이얼로그 생성
    public DatePickerDialog(JFrame parent) {
        super(parent, "날짜 선택", true);
        setLayout(new BorderLayout());

        // 날짜 선택 스피너 설정
        SpinnerDateModel model = new SpinnerDateModel();
        dateSpinner = new JSpinner(model);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));

        // 중앙 영역 구성 (날짜 선택 + 텍스트 입력)
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 날짜 선택 라벨 + 스피너 패널
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.add(new JLabel("날짜 선택:"));
        datePanel.add(dateSpinner);
        center.add(datePanel);

        // 일정 내용 입력 패널
        JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        textPanel.add(new JLabel("일정 내용:"));
        scheduleField = new JTextField(20);
        textPanel.add(scheduleField);
        center.add(Box.createVerticalStrut(10));
        center.add(textPanel);

        add(center, BorderLayout.CENTER);

        // 하단 버튼 영역 구성
        JButton ok = new JButton("확인");
        JButton cancel = new JButton("취소");

        JPanel south = new JPanel();
        south.add(ok);
        south.add(cancel);
        add(south, BorderLayout.SOUTH);

        // 버튼 이벤트: 확인 시 confirmed → true
        ok.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        cancel.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(parent);  // 부모 기준 중앙 정렬
    }

    // 확인 버튼 클릭 여부 반환
    public boolean isConfirmed() {
        return confirmed;
    }

    // 선택된 날짜 반환 (yyyy-MM-dd 형식)
    public String getSelectedDate() {
        Date date = (Date) dateSpinner.getValue();
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    // 입력된 일정 내용 반환
    public String getScheduleText() {
        return scheduleField.getText().trim();
    }
}
