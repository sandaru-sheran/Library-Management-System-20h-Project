package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection dbConnection;

    // Default credentials
    private static String dbUrl = "jdbc:mysql://localhost:3306/library_system";
    private static String dbUser = "root";
    private static String dbPass = "1234";

    private DBConnection() {
        // private constructor to enforce singleton
    }

    public static DBConnection getInstance() {
        if (dbConnection == null) {
            dbConnection = new DBConnection();
        }
        return dbConnection;
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(dbUrl, dbUser, dbPass);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver Error: MySQL Connector/J library is missing!");
            throw new SQLException("Database Driver not found.");
        }
    }

    // NEW: Method to update credentials dynamically from the Setup UI
    public static void updateCredentials(String host, String port, String database, String user, String pass) throws SQLException {
        dbUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;
        dbUser = user;
        dbPass = pass;

        // Test the new connection
        try (Connection connection = getInstance().getConnection()) {
            System.out.println("Credentials updated and connection successful.");
        }
    }
}