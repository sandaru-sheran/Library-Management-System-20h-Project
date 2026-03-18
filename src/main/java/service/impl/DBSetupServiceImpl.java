package service.impl;

import db.DBConnection;
import service.DBSetupService;

import java.sql.SQLException;

public class DBSetupServiceImpl implements DBSetupService {

    @Override
    public void testAndConnect(String host, String port, String dbName, String user, String pass) throws SQLException {
        DBConnection.updateCredentials(host, port, dbName, user, pass);
    }
}