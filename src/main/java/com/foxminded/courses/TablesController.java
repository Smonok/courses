package com.foxminded.courses;

import java.sql.SQLException;
import java.sql.Statement;

public class TablesController {
    private static final String CREATE_TABLE_FILE = "create_tables_query.sql";
    private String exceptionMessage;

    void createTable(Statement statement, String tableName) throws SQLException {
        QueriesReader reader = new QueriesReader();
        String createTable = reader.createTable(CREATE_TABLE_FILE, tableName);

        dropTable(statement, tableName);
        try {
            statement.executeUpdate(createTable);
        } catch (SQLException e) {
            exceptionMessage = String.format("Cannot create %s table", tableName);
            throw new SQLException(exceptionMessage);
        }
    }

    void dropTable(Statement statement, String tableName) throws SQLException {
        String dropTable = String.format(DatabaseConstants.DROP_TABLE, tableName);

        try {
            statement.executeUpdate(dropTable);
        } catch (SQLException e) {
            exceptionMessage = String.format("Cannot drop %s table", tableName);
            throw new SQLException(exceptionMessage);
        }
    }
}
