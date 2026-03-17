package com.techsol.database.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.techsol.database.DatabaseManager;
import com.techsol.models.DashboardAnalytics;

public class DashboardAnalyticsDao {
    public static DashboardAnalytics getDashboardAnalytics() {
        try (Connection conn = DatabaseManager.getConnection();
                Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT * FROM dashboard_analytics");
            DashboardAnalytics dashboardAnalytics = new DashboardAnalytics();
            while (rs.next()) {
                dashboardAnalytics.setTotalTestCases(rs.getInt(1));
                dashboardAnalytics.setTotalTestFiles(rs.getInt(2));
                dashboardAnalytics.setTotalTestFileSize(rs.getLong(3));
                dashboardAnalytics.setTotalTestTime(rs.getInt(4));
                dashboardAnalytics.setAverageTestTime(rs.getInt(5));
                dashboardAnalytics.setLongestTestTime(rs.getInt(6));
                dashboardAnalytics.setShortestTestTime(rs.getInt(7));
            }

            return dashboardAnalytics;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
