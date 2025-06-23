package service.DAO;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import model.Schedule;

/**
 * 일정 데이터 DB 접근 담당 클래스 (DAO)
 * 
 * - SQLite 기반 schedule 테이블 생성 및 관리
 * - 일정 저장, 전체 조회, roomId로 조건 조회
 * 
 * DAO 패턴 구조
 * - JDBC 직접 사용
 * - 모델(Schedule) 객체와 연동
 */


public class ScheduleDatabase {
    private static final String DB_URL = "jdbc:sqlite:game.db";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final ScheduleDatabase instance = new ScheduleDatabase();
    
    static {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS schedule (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "room_id INTEGER NOT NULL," +
                    "creator_id TEXT NOT NULL," +
                    "other_id TEXT NOT NULL," +
                    "date TEXT NOT NULL," +
                    "content TEXT NOT NULL," +
                    "timestamp TEXT DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveSchedule(Schedule s) {
        String sql = "INSERT INTO schedule (room_id, creator_id, other_id, date, content) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	
        	System.out.println("[DB] SaveSchedule: " + s.getCreatorId() + ", " + s.getOtherId() + ", " + s.getContent());
        	System.out.println("[DB] 저장됨: roomId = " + s.getRoomId() + ", creator = " + s.getCreatorId() + ", content = " + s.getContent());
        	
            pstmt.setInt(1, s.getRoomId());
            pstmt.setString(2, s.getCreatorId());
            pstmt.setString(3, s.getOtherId());
            pstmt.setString(4, sdf.format(s.getScheduleDate()));
            pstmt.setString(5, s.getContent());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Schedule> findByRoomId(int roomId) {
        List<Schedule> list = new ArrayList<>();
        String sql = "SELECT * FROM schedule WHERE room_id = ? ORDER BY date ASC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Schedule s = new Schedule();
                s.setRoomId(rs.getInt("room_id"));
                s.setCreatorId(rs.getString("creator_id"));
                s.setOtherId(rs.getString("other_id"));
                s.setContent(rs.getString("content"));
                s.setScheduleDate(sdf.parse(rs.getString("date")));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<Schedule> findAll() {
        List<Schedule> list = new ArrayList<>();
        String sql = "SELECT * FROM schedule ORDER BY date ASC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Schedule s = new Schedule();
                s.setRoomId(rs.getInt("room_id"));
                s.setCreatorId(rs.getString("creator_id"));
                s.setOtherId(rs.getString("other_id"));
                s.setContent(rs.getString("content"));
                s.setScheduleDate(sdf.parse(rs.getString("date")));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    
    public static ScheduleDatabase shared() {
        return instance;
    }
    
}
