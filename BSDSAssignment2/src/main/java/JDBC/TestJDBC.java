package JDBC;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by songyang on 11/15/16.
 */
public class TestJDBC {

    // Do the clear up for the database.
    public static void main(String[] args) {
        Connection conn = ConnUtil.getConnection();
//        String sql = "SELECT * FROM wordfrequency";
//        String sql = "SELECT * FROM wordfrequency ORDER BY frequency DESC LIMIT 3";
//        Statement stmt = null;
//        ResultSet rs = null;
//        try {
//            stmt = conn.createStatement();
//            rs = stmt.executeQuery(sql);
//            while (rs.next()) {
//                System.out.println(rs.getString(1) + " " + rs.getInt(2));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        WordFrequencyDAO dao = WordFrequencyDAO.getInstance();
        try {
            dao.clearUp();
//            dao.updateWordFrequency("a");
//            dao.updateWordFrequency("b");
//            dao.updateWordFrequency("c");
            System.out.println(dao.getTopNPopularWords(3));
        } catch (SQLException e) {
            e.printStackTrace();
        }

//        List<String> story = new ArrayList<String>();
//
//        File file = new File("/Users/songyang/Documents/CS6650/CS6650/BSDSAssignment2/src/main/java/JDBC/stop_words");
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(new FileReader(file));
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                String[] words = line.split(" ");
//                for (String word : words) {
//                    word = word.trim();
//                    if (word.endsWith(",") || word.endsWith(".") || word.endsWith(":") || word.endsWith(";")) {
//                        word = word.substring(0, word.length() - 1);
//                    }
//                    word = '"' + word + '"';
//                    story.add(word);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e1) {
//                }
//            }
//        }
//
//        System.out.println(story);
//        System.out.println(story.size());

    }
}
