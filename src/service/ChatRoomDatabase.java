package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomDatabase {
	private static final String DB_URL = "jdbc:sqlite:server/game.db?journal_mode=WAL&busy_timeout=5000";

	static {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA journal_mode=WAL;");
            System.out.println("✅ ChatRoomDatabase WAL 모드 설정 완료");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	
	public static int createRoomAndAddUsers(String roomName, List<String> users) {
	    int roomId = -1;
	    String insertRoomSQL = "INSERT INTO chat_room (room_name) VALUES (?)";
	    String insertMappingSQL = "INSERT INTO user_room (user_id, room_id) VALUES (?, ?)";

	    try (Connection conn = DriverManager.getConnection(DB_URL)) {
	        conn.setAutoCommit(false); // 트랜잭션 시작

	        try (
	            PreparedStatement pstmt1 = conn.prepareStatement(insertRoomSQL, Statement.RETURN_GENERATED_KEYS);
	            PreparedStatement pstmt2 = conn.prepareStatement(insertMappingSQL)
	        ) {
	            pstmt1.setString(1, roomName);
	            pstmt1.executeUpdate();
	            ResultSet rs = pstmt1.getGeneratedKeys();
	            if (rs.next())
	                roomId = rs.getInt(1);
	            else {
	                conn.rollback();
	                return -1; // 방 생성 실패
	            }

	            for (String userId : users) {
	                pstmt2.setString(1, userId);
	                pstmt2.setInt(2, roomId);
	                pstmt2.addBatch();
	            }

	            pstmt2.executeBatch();
	            conn.commit(); // 커밋

	        } catch (SQLException e) {
	            conn.rollback(); // 실패 시 롤백
	            e.printStackTrace();
	            return -1;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return -1;
	    }

	    return roomId;
	}


    // 채팅방 생성 (이름 저장) + 생성된 room_id 반환
    public static int createChatRoom(String roomName) {
    	System.out.println("[DEBUG] 현재 작업 디렉토리: " + System.getProperty("user.dir"));

        int roomId = -1;
        String insertRoomSQL = "INSERT INTO chat_room (room_name) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertRoomSQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, roomName);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                roomId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roomId;
    }

    // user_room 테이블에 userId + 방 등록
    public static void addUserToRoom(String userId, int roomId) {
        String insertMappingSQL = "INSERT INTO user_room (user_id, room_id) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertMappingSQL)) {

            pstmt.setString(1, userId);
            pstmt.setInt(2, roomId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static List<RoomInfo> getRoomsByUser(String userId) {
        List<RoomInfo> rooms = new ArrayList<>();
        String sql = """
            SELECT r.room_id, r.room_name
            FROM chat_room r
            JOIN user_room ur ON r.room_id = ur.room_id
            WHERE ur.user_id = ?
            ORDER BY r.created_at ASC
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int roomId = rs.getInt("room_id");
                String roomName = rs.getString("room_name");
                rooms.add(new RoomInfo(roomId, roomName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    // 내부 클래스로 RoomInfo 선언
    public static class RoomInfo {
        public final int roomId;
        public final String roomName;

        public RoomInfo(int roomId, String roomName) {
            this.roomId = roomId;
            this.roomName = roomName;
        }
    }

}
