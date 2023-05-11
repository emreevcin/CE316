package com.example.iae.ce316;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

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
                        "configuration_title VARCHAR(50) UNIQUE ," +
                        "programming_language VARCHAR(10)," +
                        "lecturer_code_path VARCHAR(100)," +
                        "lib VARCHAR(100)," +
                        "args VARCHAR(100)," +
                        "created_at DATE DEFAULT CURRENT_TIMESTAMP);");
                // Projects Table
                stat.executeUpdate("CREATE TABLE projects(" +
                        "project_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "project_title VARCHAR(50) UNIQUE ," +
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

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e);
        }
    }

    public ArrayList<Configuration> getAllConfigurations() throws SQLException {
        ArrayList<Configuration> configurations = new ArrayList<>();
        String query = "SELECT " +
                "configuration_title, " +
                "programming_language, " +
                "lecturer_code_path, " +
                "lib, " +
                "args " +
                "FROM configurations";

        selectSQL = conn.prepareStatement(query);
        ResultSet rs = selectSQL.executeQuery();

        while (rs.next()) {
            String title = rs.getString("configuration_title");
            String language = rs.getString("programming_language");
            String codePath = rs.getString("lecturer_code_path");
            String lib = rs.getString("lib");
            String args = rs.getString("args");
            Configuration configuration = new Configuration(title, language, lib, args, codePath);
            configurations.add(configuration);
        }

        rs.close();
        selectSQL.close();

        return configurations;
    }

    public ArrayList<Project> getAllProjects() throws SQLException {
        ArrayList<Project> projects = new ArrayList<>();
        String query = "SELECT project_title, configuration_id FROM projects";

        selectSQL = conn.prepareStatement(query);
        ResultSet rs = selectSQL.executeQuery();

        while (rs.next()) {
            String title = rs.getString("project_title");
            int configurationID = rs.getInt("configuration_id");
            Configuration c = getConfigurationByID(configurationID);
            Project p = new Project(title, c);
            projects.add(p);
        }

        rs.close();
        selectSQL.close();

        return projects;
    }

    // It returns a project_title from projects table
    public ArrayList<String> getProjectTitles() throws SQLException {
        ArrayList<String> projectTitles = new ArrayList<>();
        String query = "SELECT project_title FROM projects";

        selectSQL = conn.prepareStatement(query);
        ResultSet rs = selectSQL.executeQuery();
        while (rs.next()) {
            String projectTitle = rs.getString("project_title");
            projectTitles.add(projectTitle);
        }
        rs.close();
        selectSQL.close();

        System.out.println(projectTitles);

        return projectTitles;
    }

    // It returns a configuration_title from configurations table
    public ArrayList<String> getConfigurationTitles() throws SQLException {
        ArrayList<String> cfgTitles = new ArrayList<>();
        String query = "SELECT configuration_title FROM configurations";

        selectSQL = conn.prepareStatement(query);
        ResultSet rs = selectSQL.executeQuery();
        while (rs.next()) {
            String cfgTitle = rs.getString("configuration_title");
            cfgTitles.add(cfgTitle);
        }
        rs.close();
        selectSQL.close();

        System.out.println(cfgTitles);

        return cfgTitles;
    }

    private Configuration getConfigurationByID(int id) throws SQLException {
        Configuration configuration = null;
        String query = "SELECT " +
                "configuration_id, " +
                "configuration_title, " +
                "programming_language, " +
                "lecturer_code_path, " +
                "lib, " +
                "args " +
                "FROM configurations " +
                "WHERE configuration_id = ?";

        selectSQL = conn.prepareCall(query);
        selectSQL.setInt(1, id);

        ResultSet rs = selectSQL.executeQuery();

        if (rs.next()) {
            String title = rs.getString("configuration_title");
            String language = rs.getString("programming_language");
            String codePath = rs.getString("lecturer_code_path");
            String lib = rs.getString("lib");
            String args = rs.getString("args");

            configuration = new Configuration(title, language, lib, args, codePath);

        }

        selectSQL.close();
        rs.close();

        return configuration;
    }

        // It returns a configuration_id from configurations table using configuration_title
        private int getConfigurationID (Configuration configuration) throws SQLException {
            String query = "SELECT configuration_id " +
                    "FROM configurations " +
                    "WHERE configuration_title = ?";

            selectSQL = conn.prepareStatement(query);
            selectSQL.setString(1, configuration.getTitle());

            ResultSet rs = selectSQL.executeQuery();
            int configurationID = rs.getInt("configuration_id");

            rs.close();
            selectSQL.close();

            System.out.println(configurationID);

            return configurationID;
        }

        // It is an insert query to create an entity in projects table
        public void addProject (Project project) throws SQLException {
            int cfgID = getConfigurationID(project.getConfiguration());

            insertSQL = conn.prepareStatement("INSERT INTO projects(project_title, configuration_id) VALUES (?, ?)");
            insertSQL.setString(1, project.getTitle());
            insertSQL.setInt(2, cfgID);
            insertSQL.executeUpdate();

            insertSQL.close();

            System.out.println("Project entity added successfully!");

        }

        // It is an insert query to create an entity in configuration table
        public void addConfiguration (Configuration c) throws SQLException {
            insertSQL = conn.prepareStatement("INSERT INTO configurations(" +
                    "configuration_title, " +
                    "programming_language, " +
                    "lecturer_code_path, " +
                    "lib, " +
                    "args) " +
                    "VALUES (?, ?, ?, ?, ?)");

            insertSQL.setString(1, c.getTitle());
            insertSQL.setString(2, c.getLang());
            insertSQL.setString(3, c.getDirectory());
            insertSQL.setString(4, c.getLib());
            insertSQL.setString(5, c.getArgs());
            insertSQL.executeUpdate();

            insertSQL.close();

            System.out.println("Configuration entity added successfully!");

        }

        private static Database instance = null;

        public static Database getInstance () {
            if (instance == null) {
                instance = new Database();
            }
            return instance;
        }
}
