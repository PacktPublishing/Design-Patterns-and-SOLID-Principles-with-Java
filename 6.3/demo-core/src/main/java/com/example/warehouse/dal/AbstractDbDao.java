package com.example.warehouse.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

abstract class AbstractDbDao {

    static Connection getConnection() throws SQLException {
        String url = System.getenv("JDBC_URL");
        String user = System.getenv("JDBC_USER");
        String password = System.getenv("JDBC_PASSWORD");
        String driver = System.getenv("JDBC_DRIVER");
        try {
            // INFO: when deployed as a WAR to a servlet container like Tomcat
            // JDBC driver classes aren't loaded automatically when the driver's
            // JAR is inside the WAR.
            Class.forName(driver);
        } catch (Throwable ex) {
            throw new SQLException("Must specify the JDBC driver class to use.", ex);
        }
        return DriverManager.getConnection(url, user, password);
    }
}
