package com.techsol.database;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class DatabaseManager {
    private static final String DB_PATH = "bitsymphony.db";
    private static final String SCHEMA_FILE = "db/schema.sql";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
    }

    public static boolean initializeDatabase() {
        try (Connection connection = getConnection()) {
            InputStream inputStream = DatabaseManager.class.getClassLoader().getResourceAsStream(SCHEMA_FILE);

            if (inputStream == null) {
                throw new FileNotFoundException(SCHEMA_FILE + " (Resource not found in classpath)");
            }

            StringBuilder schema = new StringBuilder();
            try (Scanner scanner = new Scanner(inputStream)) {
                while (scanner.hasNextLine()) {
                    schema.append(scanner.nextLine()).append("\n");
                }
            }

            if (!schema.toString().isEmpty()) {
                Statement statement = connection.createStatement();
                statement.executeUpdate(schema.toString());
                System.out.println("Database initialized");

                return true;
            } else {
                System.out.println("Failed to initialize database due to missing schema file");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("Failed to initialize database: " + e.getMessage());

            return false;
        }
    }
}
