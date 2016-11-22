package JDBC;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by songyang on 11/15/16.
 */
public class TestJDBC {
    /**
     * @param args
     */
    public static void main(String[] args) {
        Connection conn = ConnUtil.getConnection();
//        String sql = "SELECT * FROM wordfrequency";
        String sql = "SELECT * FROM wordfrequency ORDER BY frequency DESC LIMIT 3";
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                System.out.println(rs.getString(1) + " " + rs.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        WordFrequencyDAO dao = WordFrequencyDAO.getInstance();
        try {
//            dao.clearUp();
            dao.updateWordFrequency("a");
            dao.updateWordFrequency("b");
            dao.updateWordFrequency("c");
            System.out.println(dao.getTopNPopularWords(3));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
