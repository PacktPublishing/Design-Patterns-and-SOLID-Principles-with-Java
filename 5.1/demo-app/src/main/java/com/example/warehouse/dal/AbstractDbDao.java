package com.example.warehouse.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

abstract class AbstractDbDao {

    private static final String DEFAULT_JDBC_URL = "jdbc:h2:mem:warehouse;DB_CLOSE_DELAY=-1";

    // INFO: on first connection the in-memory DB will be initialized by init.sql located on the classpath.
    private static final String INIT_JDBC_URL = "jdbc:h2:mem:warehouse;INIT=RUNSCRIPT FROM 'classpath:scripts/init.sql';DB_CLOSE_DELAY=-1";

    static {
        try (Connection connection = getConnection(INIT_JDBC_URL)) {
            // INFO: needed because the in-memory database gets populated on the first connection.
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IllegalStateException("This shouldn't happen.", ex);
        }
    }

    static Connection getConnection() throws SQLException {
        return getConnection(DEFAULT_JDBC_URL);
    }

    private static Connection getConnection(String jdbcUrl) throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }
}
