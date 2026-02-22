package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection dbConnection;

    private Connection connection;

    // PRIVATE Constructor - Prevents 'new DBConnection()' from other files
    private DBConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");


            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/library_system",
                    "root",
                    "1234"
            );

            System.out.println("Connection Successful: Library Database is online.");

        } catch (ClassNotFoundException e) {
            System.err.println("Driver Error: MySQL Connector/J library is missing!");
            e.printStackTrace();
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
}