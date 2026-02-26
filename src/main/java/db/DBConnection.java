package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection dbConnection;
    private Connection connection;

    // Default credentials
    private static String dbUrl = "jdbc:mysql://localhost:3306/library_system";
    private static String dbUser = "root";
    private static String dbPass = "1234";

    private DBConnection() throws SQLException {
        connect();
    }

    private void connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            System.out.println("Connection Successful: Library Database is online.");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver Error: MySQL Connector/J library is missing!");
            throw new SQLException("Database Driver not found.");
        }
    }

    public static DBConnection getInstance() throws SQLException {
        if (dbConnection == null) {
            dbConnection = new DBConnection();
        }
        return dbConnection;
    }

    public Connection getConnection() {
        return connection;
    }

    // NEW: Method to update credentials dynamically from the Setup UI
    public static void updateCredentials(String host, String port, String database, String user, String pass) throws SQLException {
        dbUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;
        dbUser = user;
        dbPass = pass;

        // Reset the singleton so it tries to connect with the new credentials
        if (dbConnection != null && dbConnection.connection != null && !dbConnection.connection.isClosed()) {
            dbConnection.connection.close();
        }
        dbConnection = new DBConnection();
    }
}