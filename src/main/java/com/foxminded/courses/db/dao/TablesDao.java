package com.foxminded.courses.db.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.foxminded.courses.constants.DatabaseConstants;
import com.foxminded.courses.util.QueriesReaderUtil;

public class TablesDao {
    private static final String CREATE_TABLE_FILE = "create_tables_query.sql";
    private final DataSource dataSource;

    public TablesDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createTable(String tableName) throws SQLException {
        String createTable = QueriesReaderUtil.createTable(CREATE_TABLE_FILE, tableName);

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()) {

            dropTable(statement, tableName);
            statement.executeUpdate(createTable);
        } catch (SQLException e) {
            throw new SQLException("Cannot create " + tableName + " table" + createTable, e);
        }
    }

    private void dropTable(Statement statement, String tableName) throws SQLException {
        String dropTable = String.format(DatabaseConstants.DROP_TABLE, tableName);

        statement.executeUpdate(dropTable);
    }
}
