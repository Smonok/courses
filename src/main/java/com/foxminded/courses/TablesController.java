package com.foxminded.courses;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class TablesController {
    private static final String CREATE_TABLE_FILE = "create_tables_query.sql";
    private final DataSource dataSource;

    public TablesController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createTable(String tableName) throws SQLException {
        QueriesReader reader = new QueriesReader();
        String createTable = reader.createTable(CREATE_TABLE_FILE, tableName);

        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            dropTable(statement, tableName);
            statement.executeUpdate(createTable);
        } catch (SQLException e) {
            throw new SQLException("Cannot create " + tableName + " table", e);
        }
    }

    private void dropTable(Statement statement, String tableName) throws SQLException {
        String dropTable = String.format(DatabaseConstants.DROP_TABLE, tableName);

        try {
            statement.executeUpdate(dropTable);
        } catch (SQLException e) {
            throw new SQLException("Cannot drop " + tableName + " table", e);
        }
    }
}
