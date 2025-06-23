package view.chat;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class GroupChatSetupDialog extends JDialog {
    private JTextField roomNameField;    // 방 이름 입력 필드
    private JList<String> friendList;    // 친구 목록 리스트

    // 생성자: 전체 친구 목록과 생성 완료 시 실행할 콜백을 전달받음
    public GroupChatSetupDialog(JFrame parent, List<String> allFriends, BiConsumer<String, List<String>> onCreated) {
        // 부모 프레임에 종속된 모달 다이얼로그 설정
        super(parent, "단체 채팅 생성", true);
        setSize(350, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));  // 여백 포함한 BorderLayout 사용

        // 상단 제목 라벨 구성
        JLabel title = new JLabel("단체 채팅방 설정", JLabel.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        // 친구 목록 리스트 구성 (다중 선택 가능)
        friendList = new JList<>(new DefaultListModel<>());
        friendList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        DefaultListModel<String> model = (DefaultListModel<String>) friendList.getModel();
        for (String name : allFriends) {
            model.addElement(name);
        }

        JScrollPane listScroll = new JScrollPane(friendList);
        listScroll.setPreferredSize(new Dimension(300, 200));

        // 방 이름 입력 필드 및 레이블 구성
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        JLabel nameLabel = new JLabel("방 이름:");
        roomNameField = new JTextField("단체 대화");

        inputPanel.add(nameLabel, BorderLayout.WEST);
        inputPanel.add(roomNameField, BorderLayout.CENTER);

        // 생성 버튼 설정 및 클릭 이벤트 처리
        JButton createBtn = new JButton("생성");
        createBtn.addActionListener(e -> {
            String roomName = roomNameField.getText().trim();
            List<String> selected = friendList.getSelectedValuesList();

            // 유효성 검사: 방 이름 및 친구 선택 여부 확인
            if (roomName.isEmpty() || selected.isEmpty()) {
                JOptionPane.showMessageDialog(this, "방 이름과 참여자를 모두 선택하세요.");
                return;
            }

            // 생성 완료 콜백 실행 후 다이얼로그 종료
            onCreated.accept(roomName, selected);
            dispose();
        });

        // 중앙 영역 구성: 친구 목록 + 방 이름 입력
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        centerPanel.add(listScroll);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(inputPanel);

        // 전체 레이아웃에 중앙, 하단 구성 요소 추가
        add(centerPanel, BorderLayout.CENTER);
        add(createBtn, BorderLayout.SOUTH);
    }
}
