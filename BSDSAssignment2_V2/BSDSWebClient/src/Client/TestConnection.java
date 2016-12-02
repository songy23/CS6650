/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author songyang
 */
public class TestConnection {
    public static void main(String[] args) {
        Connection conn = ConnUtil.getConnection();
        String sql = "SELECT * FROM wordfrequency";
        String cleanUp = "DELETE FROM WordFrequency";;
//        String sql = "SELECT * FROM wordfrequency ORDER BY frequency DESC LIMIT 3";
        Statement stmt = null;
        ResultSet rs = null;
        PreparedStatement deleteStmt = null;
        try {
//            stmt = conn.createStatement();
//            rs = stmt.executeQuery(sql);
            
            deleteStmt = conn.prepareStatement(cleanUp);
            deleteStmt.executeUpdate();
            System.out.println(rs);
//            while (rs.next()) {
//                System.out.println(rs.getString(1) + " " + rs.getInt(2));
//            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
}
