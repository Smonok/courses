package com.foxminded.courses;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Initializer {
    private final String USER = "postgres";
    private final String PASSWORD = "32147";
    private final String SERVER_NAME = "localhost";
    private final String DATABASE_NAME = "courses";
    private final Logger LOG = LoggerFactory.getLogger(Initializer.class);

    public void startApplication() {
        try (Connection connection = connectToDatabase()) {
            try (Statement statement = connection.createStatement()) {
                TablesFillerUtil.fillAllTables(statement);
                new Menu().workWithApplication(statement);

                try {
                    statement.close();
                } catch (SQLException e) {
                    LOG.error("Cannot close statement");
                }
            } catch (SQLException e) {
                LOG.error("Cannot create statement");
            }

            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("Cannot close connection");
            }
        } catch (SQLException e1) {
            LOG.error("Cannot create connection");
        }
    }

    private Connection connectToDatabase() throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName(SERVER_NAME);
        dataSource.setDatabaseName(DATABASE_NAME);
        dataSource.setUser(USER);
        dataSource.setPassword(PASSWORD);

        Connection connection = dataSource.getConnection();

        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            LOG.error("Cannot connect to database", e);
            throw e;
        }

        return connection;
    }
}
