package course_management.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConnector {
    private static final String DRIVER_PATH = "oracle.jdbc.driver.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@localhost:1521/xe";
    private static final String USER_NAME = "c##dbsoft";
    private static final String PASSWORD = "1234";
    static Connection con;

    public static Connection getConnection() {
        try {
            Class.forName(DRIVER_PATH);
            con = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver Class Not Found");
        } catch (SQLException e) {
            System.out.println("JDBC Connection Failed");
        }
        return con;
    }
}