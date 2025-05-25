package com.wandaymo.dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/";
    private static final String DEFAULT_DB = "postgres";
    private static final String TARGET_DB = "vendamanager";
    private static final String USER = "postgres";
    private static final String PASSWORD = "sysadmin";

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL + TARGET_DB, USER, PASSWORD);
        } catch (SQLException e) {
            if (e.getMessage().contains("database \"" + TARGET_DB + "\" does not exist")) {
                createDatabase();
                return DriverManager.getConnection(URL + TARGET_DB, USER, PASSWORD);
            }
            throw e;
        }
    }

    private static void createDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL + DEFAULT_DB, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE DATABASE " + TARGET_DB);
        }
    }

    public static void createTables() {
        try (Connection conn = getConnection();
             InputStream is = DatabaseConnection.class.getResourceAsStream("/sql/create_tables.sql");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            String[] statements = sb.toString().split(";");
            for (String statement : statements) {
                if (!statement.trim().isEmpty()) {
                    try (PreparedStatement stmt = conn.prepareStatement(statement)) {
                        stmt.execute();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}