package com.foxminded.courses;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;

public final class DataSourceCustomizer {
    private static final String USER = "postgres";
    private static final String PASSWORD = "32147";
    private static final String SERVER_NAME = "localhost";
    private static final String DATABASE_NAME = "courses";

    private DataSourceCustomizer() {
        throw new IllegalStateException();
    }

    public static DataSource customizeDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName(SERVER_NAME);
        dataSource.setDatabaseName(DATABASE_NAME);
        dataSource.setUser(USER);
        dataSource.setPassword(PASSWORD);

        return dataSource;
    }
}
