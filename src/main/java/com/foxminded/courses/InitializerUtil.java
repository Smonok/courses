package com.foxminded.courses;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitializerUtil {
    private static final String USER = "postgres";
    private static final String PASSWORD = "32147";
    private static final String SERVER_NAME = "localhost";
    private static final String DATABASE_NAME = "courses";
    private static final PGSimpleDataSource dataSource = new PGSimpleDataSource();
    public static final Logger log = LoggerFactory.getLogger(InitializerUtil.class);

    public static void startApp() {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDatabase();
            try {
                statement = connection.createStatement();
            } catch (SQLException e) {
                log.error("Cannot create statement");
            }

            TablesFillerUtil.fillAllTables(statement);
            new Menu().displayMenu(statement);
        } catch (SQLException e1) {
            e1.getMessage();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                log.error("Cannot close statement");
            }
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("Cannot close connection");
            }
        }
    }

    private static Connection connectToDatabase() throws SQLException {
        dataSource.setServerName(SERVER_NAME);
        dataSource.setDatabaseName(DATABASE_NAME);
        dataSource.setUser(USER);
        dataSource.setPassword(PASSWORD);
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            log.error("Cannot connect to database", e);
            throw new SQLException("Cannot connect to database", e);
        }

        return connection;
    }
}
