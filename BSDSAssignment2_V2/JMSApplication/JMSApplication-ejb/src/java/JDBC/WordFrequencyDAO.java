package JDBC;

import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by songyang on 11/15/16.
 */
@Singleton
public class WordFrequencyDAO {

    // Singeleton DAO
    private static WordFrequencyDAO instance = null;

    private WordFrequencyDAO() {}

    public static WordFrequencyDAO getInstance() {
        if (instance == null) {
            instance = new WordFrequencyDAO();
        }
        return instance;
    }

    private static Set<String> localCachedWords = new HashSet<String>();

    public void updateWordFrequency(String word) throws SQLException {
        Connection connection = null;

        try {
            connection = ConnUtil.getConnection();
            if (!localCachedWords.contains(word) && getFrequency(word, connection) == 0) {
                insertWord(word, connection);
            } else {
                localCachedWords.add(word);
                incrementFrequency(word, connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(connection != null) {
                connection.close();
            }
        }
    }

    private void insertWord(String word, Connection connection) throws SQLException {
        String insertWord = "INSERT INTO WordFrequency(word, frequency) VALUES(?,?);";
        PreparedStatement insertStmt = null;

        try {
            insertStmt = connection.prepareStatement(insertWord);
            insertStmt.setString(1, word);
            insertStmt.setInt(2, 1);
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(insertStmt != null) {
                insertStmt.close();
            }
        }
    }

    private int getFrequency(String word, Connection connection) throws SQLException {
        String getFrequency = "SELECT frequency FROM WordFrequency WHERE word=?;";
        PreparedStatement selectStmt = null;
        ResultSet results = null;

        try {
            selectStmt = connection.prepareStatement(getFrequency);
            selectStmt.setString(1, word);
            results = selectStmt.executeQuery();
            if (results.next()) {
                return results.getInt("frequency");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(selectStmt != null) {
                selectStmt.close();
            }
            if(results != null) {
                results.close();
            }
        }

        return 0;
    }

    private void incrementFrequency(String word, Connection connection) throws SQLException {
        String incrementFrequency = "UPDATE wordfrequency SET frequency = frequency + 1 WHERE word =?;";
        PreparedStatement updateStmt = null;

        try {
            updateStmt = connection.prepareStatement(incrementFrequency);
            updateStmt.setString(1, word);
            updateStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(updateStmt != null) {
                updateStmt.close();
            }
        }
    }

    public String getTopNPopularWords(int num) throws SQLException {
        String getTopNPopularWords = "SELECT word FROM WordFrequency ORDER BY frequency DESC LIMIT ?;";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        StringBuilder words = new StringBuilder();

        try {
            connection = ConnUtil.getConnection();
            assert connection != null;
            selectStmt = connection.prepareStatement(getTopNPopularWords);
            selectStmt.setInt(1, num);
            results = selectStmt.executeQuery();
            while (results.next()) {
                words.append(results.getString("word")).append(' ');
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(connection != null) {
                connection.close();
            }
            if(selectStmt != null) {
                selectStmt.close();
            }
            if(results != null) {
                results.close();
            }
        }

        return words.toString();
    }

    public void clearUp() throws SQLException {
        String deleteWords = "DELETE FROM WordFrequency";
        Connection connection = null;
        PreparedStatement deleteStmt = null;
        try {
            connection = ConnUtil.getConnection();
            deleteStmt = connection.prepareStatement(deleteWords);
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(deleteStmt != null) {
                deleteStmt.close();
            }
        }
    }
}
