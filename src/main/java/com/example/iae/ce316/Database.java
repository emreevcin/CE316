package com.example.iae.ce316;

import java.io.File;
import java.sql.*;

public class Database {

    private final String fileName;
    private Connection conn;
    private PreparedStatement insertSQL;
    private PreparedStatement selectSQL;

    private Database() {
        fileName = "./src/main/mydb.db";
        File file = new File(fileName);
        boolean firstRun = !file.exists();
        conn = null;

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + fileName);

            if (firstRun) {
                Statement stat = conn.createStatement();
                // Enables foreign keys feature for sqlite
                stat.executeUpdate("PRAGMA foreign_keys = ON;");
                // Configurations Table
                stat.executeUpdate("CREATE TABLE configurations(" +
                        "configuration_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "configuration_title VARCHAR(50)," +
                        "configuration_directory VARCHAR(100)," +
                        "programming_language VARCHAR(10)," +
                        "lecturer_code_path VARCHAR(100)," +
                        "lib VARCHAR(100)," +
                        "args VARCHAR(100)," +
                        "created_at DATE DEFAULT CURRENT_TIMESTAMP);");
                // Projects Table
                stat.executeUpdate("CREATE TABLE projects(" +
                        "project_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "project_title VARCHAR(50)," +
                        "created_at DATE DEFAULT CURRENT_TIMESTAMP," +
                        "configuration_id INTEGER NOT NULL," +
                        "FOREIGN KEY (configuration_id) REFERENCES configurations(configuration_id));");
                // Submissions Table
                stat.executeUpdate("CREATE TABLE submissions(" +
                        "student_id VARCHAR(20) PRIMARY KEY," +
                        "status VARCHAR(10)," +
                        "error VARCHAR(100)," +
                        "student_code_path VARCHAR(100)," +
                        "created_at DATE DEFAULT CURRENT_TIMESTAMP," +
                        "project_id INTEGER NOT NULL," +
                        "FOREIGN KEY (project_id) REFERENCES projects(project_id));");
            }

            /*
            insertSQL = conn.prepareStatement("");
            selectSQL = conn.prepareStatement("");
             */

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e);
        }
    }

    private static Database instance = null;

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
}
