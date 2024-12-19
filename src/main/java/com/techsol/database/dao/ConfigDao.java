package com.techsol.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.techsol.database.DatabaseManager;

public class ConfigDao {
    public static Map<String, String> getConfig() {
        Map<String, String> config = new HashMap<>();
        String query = "SELECT key, value FROM app_config";
        try (Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                config.put(rs.getString("key"), rs.getString("value"));
            }

            return config;
        } catch (SQLException e) {
            e.printStackTrace();

            System.err.println("Failed to fetch config: " + e.getMessage());
            return null;
        }
    }

    public static boolean updateConfig(String key, String value) {
        String query = "UPDATE app_config SET value = ? WHERE key = ?";
        try (Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, key);
            stmt.setString(2, value);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to update config: " + e.getMessage());
        }

        return false;
    }
}
