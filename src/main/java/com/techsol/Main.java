package com.techsol;

import java.util.Map;

import com.techsol.database.DatabaseManager;
import com.techsol.database.dao.ConfigDao;

public class Main {

    public static void main(String[] args) {
        // Start of by fetching the configs from the database
        Map<String, String> configs = ConfigDao.getConfig();

        // Check if the configs exist.
        // If they don't exist, the database does not exist most likely.
        // In this case, perform one trial to create the database.
        int i = 0;
        while (configs == null && i < 1) {
            System.out.println("Database does not exist...");
            System.out.println("Creating database using the default schema");
            if (DatabaseManager.initializeDatabase()) {
                configs = ConfigDao.getConfig();
            }
            i++;
        }

        // Check if the configs are null and database creation was attempted at least
        // once.
        // If it was attempted and failed (configs are null), then stop the application.
        if (configs == null && i == 1) {
            System.out.println("Database failed to create");
            System.out.println("Stopping application. Goodbye.");
            return;
        }

    }
}
