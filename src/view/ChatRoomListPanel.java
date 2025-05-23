package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;

public class ChatRoomListPanel extends JPanel {
    private DefaultListModel<String> roomListModel = new DefaultListModel<>();
    private JList<String> roomList = new JList<>(roomListModel);
    private BiConsumer<String, Integer> onRoomSelected; // (roomName, roomId)

    public ChatRoomListPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel title = new JLabel("채팅방 목록");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        roomList.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        roomList.setSelectionBackground(new Color(220, 240, 255));
        JScrollPane scrollPane = new JScrollPane(roomList);

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

        add(title, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addRoom(String roomName, int roomId) {
        roomListModel.addElement(formatRoomName(roomName, roomId));
    }

    private String formatRoomName(String name, int id) {
        return name + " [ID:" + id + "]";
    }

    private int extractRoomId(String displayName) {
        // 예: "test1님과의 대화 [ID:42]" → 42
        try {
            int start = displayName.indexOf("[ID:") + 4;
            int end = displayName.indexOf("]");
            return Integer.parseInt(displayName.substring(start, end));
        } catch (Exception e) {
            return -1;
        }
    }

    public void setRoomSelectionHandler(BiConsumer<String, Integer> handler) {
        this.onRoomSelected = handler;
    }
}