package com.foxminded.courses;

import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;

public class ApplicationStarter {
    private static final Logger LOG = getLogger(ApplicationStarter.class);

    public static void startApplication() {
        try (Connection connection = DatabaseConnector.connectToDatabase()) {
            initialize(connection);
        } catch (SQLException e1) {
            LOG.error("Cannot create connection", e1);
        }
    }

    private static void initialize(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            TablesUtil.fillAllTables(statement);
            Menu.workWithApplication(statement);
        } catch (SQLException e) {
            LOG.error("Cannot create statement", e);
        }
    }

    private ApplicationStarter() {
        throw new IllegalStateException();
    }
}
