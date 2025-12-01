package com.techsol.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.techsol.database.DatabaseManager;
import com.techsol.models.TestResult;

public class TestResultDao {
    public static boolean createTestResult(TestResult testResult) {
        String INSERT_QUERY = "INSERT INTO test_result (test_name, algorithm_name, start_time, end_time," +
                "duration_ms, cpu_usage, memory_usage, disk_io, extra_info, session_id, test_file_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement stmt = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, testResult.getTestName());
                    stmt.setString(2, testResult.getAlgorithmName());
                    stmt.setString(3, testResult.getStartTime());
                    stmt.setString(4, testResult.getEndTime());
                    stmt.setDouble(5, testResult.getDurationMs());
                    stmt.setDouble(6, testResult.getCpuUsage());
                    stmt.setLong(7, testResult.getMemoryUsage());
                    stmt.setDouble(8, testResult.getDiskIO());
                    stmt.setString(9, testResult.getExtraInfo());
                    stmt.setInt(10, testResult.getSessionId());
                    stmt.setInt(11, testResult.getFileId());

                    stmt.executeUpdate();

                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    } else {
                        return false;
                    }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
