package com.techsol.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.techsol.database.DatabaseManager;
import com.techsol.models.TestFile;

public class TestFileDao {
    public static int saveTestFile(TestFile testFile) {
        String INSERT_QUERY = "INSERT INTO test_file (file_name, file_path, file_exists, file_size_in_bytes, number_of_items, data_type, created_at, updated_at)"
                +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);) {
            stmt.setString(1, testFile.getFileName());
            stmt.setString(2, testFile.getFilePath());
            stmt.setInt(3, testFile.getFileExists());
            stmt.setLong(4, testFile.getFileSizeInBytes());
            stmt.setLong(5, testFile.getNumberOfItems());
            stmt.setString(6, testFile.getDataType());
            stmt.setString(7, testFile.getCreatedAt().toString());
            stmt.setString(8, testFile.getUpdatedAt().toString());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
