package com.foxminded.courses;

import java.sql.Connection;
import java.sql.SQLException;

import org.postgresql.ds.PGSimpleDataSource;

public class DatabaseConnector {
    private static final String USER = "postgres";
    private static final String PASSWORD = "32147";
    private static final String SERVER_NAME = "localhost";
    private static final String DATABASE_NAME = "courses";

    static Connection connectToDatabase() throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName(SERVER_NAME);
        dataSource.setDatabaseName(DATABASE_NAME);
        dataSource.setUser(USER);
        dataSource.setPassword(PASSWORD);

        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new SQLException("Cannot connect to database");
        }
    }

    private DatabaseConnector() {
        throw new IllegalStateException();
    }
}
