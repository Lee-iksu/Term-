package view.schedule;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class DatePickerDialog extends JDialog {
    private JSpinner dateSpinner;
    private JTextField scheduleField;
    private boolean confirmed = false;

    public DatePickerDialog(JFrame parent) {
        super(parent, "날짜 선택", true);
        setLayout(new BorderLayout());

        // 날짜 선택 부분
        SpinnerDateModel model = new SpinnerDateModel();
        dateSpinner = new JSpinner(model);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.add(new JLabel("날짜 선택:"));
        datePanel.add(dateSpinner);
        center.add(datePanel);

        // 일정 내용 입력란 추가
        JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        textPanel.add(new JLabel("일정 내용:"));
        scheduleField = new JTextField(20);
        textPanel.add(scheduleField);
        center.add(Box.createVerticalStrut(10));
        center.add(textPanel);

        add(center, BorderLayout.CENTER);

        // 확인 / 취소 버튼
        JButton ok = new JButton("확인");
        JButton cancel = new JButton("취소");

        JPanel south = new JPanel();
        south.add(ok);
        south.add(cancel);
        add(south, BorderLayout.SOUTH);

        ok.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        cancel.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(parent);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getSelectedDate() {
        Date date = (Date) dateSpinner.getValue();
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public String getScheduleText() {
        return scheduleField.getText().trim();
    }
}
