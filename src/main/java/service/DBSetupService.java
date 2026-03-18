package service;

import java.sql.SQLException;

public interface DBSetupService {
    void testAndConnect(String host, String port, String dbName, String user, String pass) throws SQLException;
}