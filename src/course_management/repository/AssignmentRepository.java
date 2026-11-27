package course_management.repository;

import course_management.domain.Assignment;

import java.sql.*;
import java.util.ArrayList;

public class AssignmentRepository {
    ArrayList<Assignment> assignmentList;

    // 전체 또는 미완료 과제 조회
    public ArrayList<Assignment> select(boolean onlyIncomplete) {
        assignmentList = new ArrayList<>();
        Connection con = JDBCConnector.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT a.assignment_id, a.course_id, c.course_name, a.start_date, a.due_date, a.content, a.status " +
                "FROM ASSIGNMENT a, COURSE c WHERE a.course_id = c.course_id ";
        if (onlyIncomplete) {
            sql += "AND a.status = 'INCOMPLETE' ";
        }
        sql += "ORDER BY a.due_date ASC";

        try {
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                assignmentList.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(rs, pstmt, con);
        }
        return assignmentList;
    }

    // [신규 추가] 특정 과목(course_id)의 과제만 모아서 조회
    public ArrayList<Assignment> selectByCourseId(int courseId) {
        assignmentList = new ArrayList<>();
        Connection con = JDBCConnector.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT a.assignment_id, a.course_id, c.course_name, a.start_date, a.due_date, a.content, a.status " +
                "FROM ASSIGNMENT a, COURSE c " +
                "WHERE a.course_id = c.course_id AND a.course_id = ? " +
                "ORDER BY a.due_date ASC";

        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                assignmentList.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(rs, pstmt, con);
        }
        return assignmentList;
    }

    private Assignment mapRow(ResultSet rs) throws SQLException {
        Assignment a = new Assignment();
        a.setAssignment_id(rs.getInt("assignment_id"));
        a.setCourse_id(rs.getInt("course_id"));
        a.setCourse_name(rs.getString("course_name"));
        a.setStart_date(rs.getDate("start_date"));
        a.setDue_date(rs.getDate("due_date"));
        a.setContent(rs.getString("content"));
        a.setStatus(rs.getString("status"));
        return a;
    }

    public void insert(Assignment a) {
        Connection con = JDBCConnector.getConnection();
        PreparedStatement pstmt = null;
        String sql = "INSERT INTO ASSIGNMENT VALUES (SEQ_ASSIGNMENT.NEXTVAL, ?, ?, ?, ?, 'INCOMPLETE')";

        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, a.getCourse_id());
            pstmt.setDate(2, a.getStart_date());
            pstmt.setDate(3, a.getDue_date());
            pstmt.setString(4, a.getContent());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(null, pstmt, con);
        }
    }

    public void update(Assignment a) {
        Connection con = JDBCConnector.getConnection();
        PreparedStatement pstmt = null;
        String sql = "UPDATE ASSIGNMENT SET start_date = ?, due_date = ?, content = ?, status = ? WHERE assignment_id = ?";

        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setDate(1, a.getStart_date());
            pstmt.setDate(2, a.getDue_date());
            pstmt.setString(3, a.getContent());
            pstmt.setString(4, a.getStatus());
            pstmt.setInt(5, a.getAssignment_id());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(null, pstmt, con);
        }
    }

    public void delete(int assignmentId) {
        Connection con = JDBCConnector.getConnection();
        PreparedStatement pstmt = null;
        String sql = "DELETE FROM ASSIGNMENT WHERE assignment_id = ?";

        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, assignmentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(null, pstmt, con);
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