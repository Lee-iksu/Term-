// 채팅방 생성 시 사용하는 팝업 다이얼로그
// 사용자로부터 채팅방 이름을 입력받고 생성 버튼을 통해 콜백 실행
package view.chat;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class ChatRoomSetupDialog extends JDialog {

    // 다이얼로그 생성자: 부모 프레임, 대상 사용자 ID, 채팅방 생성 시 실행할 콜백 전달
    public ChatRoomSetupDialog(JFrame parent, String targetId, Consumer<String> onRoomCreated) {
        // 모달 다이얼로그 설정: 부모 프레임에 종속되고 사용자 입력 필요
        super(parent, "채팅방 설정", true);

        // 다이얼로그 크기 및 위치 지정
        setSize(300, 150);
        setLocationRelativeTo(parent);  // 부모 창 기준 중앙 배치

        // 전체 레이아웃: 여백 포함한 BorderLayout 사용
        setLayout(new BorderLayout(10, 10));

        // 설명 라벨 설정
        JLabel label = new JLabel("채팅방 이름 입력:");

        // 입력 필드: 기본값은 'targetId님과의 대화'
        JTextField roomNameField = new JTextField(targetId + "님과의 대화");

        // 생성 버튼 클릭 시 채팅방 이름을 받아 콜백 실행 후 닫기
        JButton createButton = new JButton("생성");
        createButton.addActionListener(e -> {
            String roomName = roomNameField.getText().trim();
            if (!roomName.isEmpty()) {
                onRoomCreated.accept(roomName);  // 외부 콜백 호출
                dispose();                       // 다이얼로그 종료
            }
        });

        // 입력 관련 컴포넌트를 담는 패널 구성
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 내부 여백

        inputPanel.add(label, BorderLayout.NORTH);
        inputPanel.add(roomNameField, BorderLayout.CENTER);
        inputPanel.add(createButton, BorderLayout.SOUTH);

        // 메인 다이얼로그에 입력 패널 추가
        add(inputPanel, BorderLayout.CENTER);
    }
}
