package course_management.repository;

import course_management.domain.CourseSchedule;

import java.sql.*;
import java.util.ArrayList;

public class CourseRepository {
    ArrayList<CourseSchedule> courseList;

    // 전체 강의 및 시간표 조회
    public ArrayList<CourseSchedule> selectAll() {
        courseList = new ArrayList<>();
        Connection con = JDBCConnector.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT c.course_id, c.course_name, s.schedule_id, s.day_of_week, s.start_time, s.end_time " +
                "FROM COURSE c, LECTURE_SCHEDULE s " +
                "WHERE c.course_id = s.course_id ORDER BY s.day_of_week ASC, s.start_time ASC";

        try {
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                CourseSchedule cs = new CourseSchedule();
                cs.setCourse_id(rs.getInt("course_id"));
                cs.setCourse_name(rs.getString("course_name"));
                cs.setSchedule_id(rs.getInt("schedule_id"));
                cs.setDay_of_week(rs.getInt("day_of_week"));
                cs.setStart_time(rs.getString("start_time"));
                cs.setEnd_time(rs.getString("end_time"));
                courseList.add(cs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(rs, pstmt, con);
        }
        return courseList;
    }

    // 강의 및 시간표 삽입
    public void insert(CourseSchedule cs) {
        Connection con = JDBCConnector.getConnection();
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;

        String sqlCourse = "INSERT INTO COURSE (course_id, course_name) VALUES (SEQ_COURSE.NEXTVAL, ?)";
        String sqlSchedule = "INSERT INTO LECTURE_SCHEDULE (schedule_id, course_id, day_of_week, start_time, end_time) " +
                "VALUES (SEQ_SCHEDULE.NEXTVAL, SEQ_COURSE.CURRVAL, ?, ?, ?)";

        try {
            con.setAutoCommit(false);
            pstmt1 = con.prepareStatement(sqlCourse);
            pstmt1.setString(1, cs.getCourse_name());
            pstmt1.executeUpdate();

            pstmt2 = con.prepareStatement(sqlSchedule);
            pstmt2.setInt(1, cs.getDay_of_week());
            pstmt2.setString(2, cs.getStart_time());
            pstmt2.setString(3, cs.getEnd_time());
            pstmt2.executeUpdate();

            con.commit();
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException(e);
        } finally {
            close(null, pstmt2, null);
            close(null, pstmt1, con);
        }
    }

    // 강의 및 시간표 수정
    public void update(CourseSchedule cs) {
        Connection con = JDBCConnector.getConnection();
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;

        String sqlCourse = "UPDATE COURSE SET course_name = ? WHERE course_id = ?";
        String sqlSchedule = "UPDATE LECTURE_SCHEDULE SET day_of_week = ?, start_time = ?, end_time = ? WHERE schedule_id = ?";

        try {
            con.setAutoCommit(false);
            pstmt1 = con.prepareStatement(sqlCourse);
            pstmt1.setString(1, cs.getCourse_name());
            pstmt1.setInt(2, cs.getCourse_id());
            pstmt1.executeUpdate();

            pstmt2 = con.prepareStatement(sqlSchedule);
            pstmt2.setInt(1, cs.getDay_of_week());
            pstmt2.setString(2, cs.getStart_time());
            pstmt2.setString(3, cs.getEnd_time());
            pstmt2.setInt(4, cs.getSchedule_id());
            pstmt2.executeUpdate();

            con.commit();
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException(e);
        } finally {
            close(null, pstmt2, null);
            close(null, pstmt1, con);
        }
    }

    // 강의 삭제 (자식 레코드 포함 삭제)
    public void delete(int courseId) {
        Connection con = JDBCConnector.getConnection();
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmt3 = null;

        try {
            con.setAutoCommit(false);
            pstmt1 = con.prepareStatement("DELETE FROM ASSIGNMENT WHERE course_id = ?");
            pstmt1.setInt(1, courseId);
            pstmt1.executeUpdate();

            pstmt2 = con.prepareStatement("DELETE FROM LECTURE_SCHEDULE WHERE course_id = ?");
            pstmt2.setInt(1, courseId);
            pstmt2.executeUpdate();

            pstmt3 = con.prepareStatement("DELETE FROM COURSE WHERE course_id = ?");
            pstmt3.setInt(1, courseId);
            pstmt3.executeUpdate();

            con.commit();
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException(e);
        } finally {
            close(null, pstmt3, null);
            close(null, pstmt2, null);
            close(null, pstmt1, con);
        }
    }

    public String toDayName(int day) {
        String[] days = {"일", "월", "화", "수", "목", "금", "토", "일"};
        return (day >= 0 && day <= 7) ? days[day] : "";
    }

    public int toDayInt(String dayName) {
        switch (dayName) {
            case "월": return 1;
            case "화": return 2;
            case "수": return 3;
            case "목": return 4;
            case "금": return 5;
            case "토": return 6;
            case "일": return 7;
            default: return 1;
        }
    }

    private void close(ResultSet rs, PreparedStatement pstmt, Connection con) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}