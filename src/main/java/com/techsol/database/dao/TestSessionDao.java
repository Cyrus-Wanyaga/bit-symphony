package com.techsol.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.techsol.database.DatabaseManager;
import com.techsol.models.TestSession;

public class TestSessionDao {
    public static int createTestSession(TestSession testSession) {
        String INSERT_QUERY = "INSERT INTO test_session (test_group, description, total_runs) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement stmt = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, testSession.getTestGroup());
            stmt.setString(2, testSession.getDescription() != null ? testSession.getDescription() : "");
            stmt.setInt(3, testSession.getTotalRuns());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
