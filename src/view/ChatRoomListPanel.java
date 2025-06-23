package view.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;

public class ChatRoomListPanel extends JPanel {
    private DefaultListModel<String> roomListModel = new DefaultListModel<>();  // 채팅방 목록 모델
    private JList<String> roomList = new JList<>(roomListModel);                // 채팅방 목록 컴포넌트
    private BiConsumer<String, Integer> onRoomSelected;                         // 채팅방 선택 시 처리 함수 (roomName, roomId)
    private JButton groupCreateButton;                                          // 그룹 생성 버튼
    private Runnable onGroupCreateClicked;                                      // 그룹 생성 핸들러

    public ChatRoomListPanel() {
        // 전체 패널 레이아웃 설정
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 상단 영역: 제목 + 그룹 생성 버튼
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        // 좌측 제목 라벨
        JLabel title = new JLabel("채팅방 목록");
        title.setFont(new Font("마리오 고딕", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 우측 그룹 생성 버튼 설정
        groupCreateButton = new JButton("단체 채팅 생성");
        groupCreateButton.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        groupCreateButton.setFocusPainted(false);
        groupCreateButton.setBackground(new Color(102, 204, 204));
        groupCreateButton.setForeground(Color.WHITE);
        groupCreateButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // 그룹 생성 버튼 클릭 시 핸들러 실행
        groupCreateButton.addActionListener(e -> {
            if (onGroupCreateClicked != null) {
                onGroupCreateClicked.run();
            }
        });

        // 상단 영역에 제목 및 버튼 배치
        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(groupCreateButton, BorderLayout.EAST);

        // 채팅방 목록 스타일 설정
        roomList.setFont(new Font("마리오 고딕", Font.PLAIN, 13));
        roomList.setSelectionBackground(new Color(220, 240, 255));

        // 스크롤 가능한 목록으로 구성
        JScrollPane scrollPane = new JScrollPane(roomList);

        // 더블 클릭 시 채팅방 선택 처리
        roomList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = roomList.getSelectedIndex();
                    String selected = roomList.getSelectedValue();
                    if (selected != null && onRoomSelected != null) {
                        int roomId = extractRoomId(selected); // 방 이름에서 ID 추출
                        onRoomSelected.accept(selected, roomId);
                    }
                }
            }
        });

        // 컴포넌트 배치
        add(topPanel, BorderLayout.NORTH);     // 상단: 제목 + 버튼
        add(scrollPane, BorderLayout.CENTER);  // 중앙: 채팅방 목록
    }

    // 채팅방을 목록에 추가 (방 이름 + ID 포맷)
    public void addChatRoom(String roomName, int roomId) {
        roomListModel.addElement(formatRoomName(roomName, roomId));
    }

    // 다른 이름의 동일 기능 메서드 (중복된 듯함)
    public void addRoom(String roomName, int roomId) {
        roomListModel.addElement(formatRoomName(roomName, roomId));
    }

    // 그룹 생성 버튼 클릭 시 호출될 외부 핸들러 등록
    public void setGroupRoomCreationHandler(Runnable handler) {
        this.onGroupCreateClicked = handler;
    }

    // 방 이름과 ID를 "[ID:숫자]" 형식으로 포맷
    private String formatRoomName(String name, int id) {
        return name + " [ID:" + id + "]";
    }

    // 포맷된 방 이름에서 ID만 추출
    private int extractRoomId(String displayName) {
        try {
            int start = displayName.indexOf("[ID:") + 4;
            int end = displayName.indexOf("]");
            return Integer.parseInt(displayName.substring(start, end));
        } catch (Exception e) {
            return -1;
        }
    }

    // 채팅방 선택 시 실행할 핸들러 등록
    public void setRoomSelectionHandler(BiConsumer<String, Integer> handler) {
        this.onRoomSelected = handler;
    }
}
