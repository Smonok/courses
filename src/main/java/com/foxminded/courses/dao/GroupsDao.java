package com.foxminded.courses.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringJoiner;

import javax.sql.DataSource;

import com.foxminded.courses.DatabaseConstants;

public class GroupsDao {
    private static final String ERROR_MESSAGE = "No groups found";
    private static final String GROUP_ID = "group_id";
    private static final String GROUP_NAME = "group_name";
    private static final String COUNT = "count";
    private final DataSource dataSource;
    private ResultSet resultSet;

    public GroupsDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String selectGroupsWithStudentsNumber(int studentsNumber, int lessOrEquals) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            String groupsWithStudentsCount = modifyGroupsWithStudentsCountQuery(studentsNumber, lessOrEquals);
            resultSet = statement.executeQuery(groupsWithStudentsCount);

            return parseGroupsWithStudentsNumber();
        } catch (SQLException e) {
            throw new SQLException("Cannot find all groups with " + studentsNumber + " students.", e);
        }
    }

    private String modifyGroupsWithStudentsCountQuery(int studentsNumber, int input) {
        String groupsWithStudentsCount = "";

        if (input == 1) {
            groupsWithStudentsCount = String.format(DatabaseConstants.GROUPS_WITH_STUDENTS_COUNT, '<', studentsNumber);
        } else if (input == 2) {
            groupsWithStudentsCount = String.format(DatabaseConstants.GROUPS_WITH_STUDENTS_COUNT, '=', studentsNumber);
        }

        return groupsWithStudentsCount;
    }

    private String parseGroupsWithStudentsNumber() throws SQLException {
        StringJoiner groupWithStudents = new StringJoiner("\n");

        while (resultSet.next()) {
            String groupName = resultSet.getString(GROUP_NAME);
            int count = resultSet.getInt(COUNT);

            groupWithStudents.add(String.format("%s <==> %d", groupName, count));
        }

        if (groupWithStudents.toString().isEmpty()) {
            return ERROR_MESSAGE;
        }

        return groupWithStudents.toString();
    }

    public String selectAllGroups() throws SQLException {
        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery(DatabaseConstants.ALL_GROUPS);

            return parseGroupsInfo();
        } catch (SQLException e) {
            throw new SQLException("Cannot select all groups.", e);
        }
    }

    private String parseGroupsInfo() throws SQLException {
        StringJoiner groups = new StringJoiner("\n");

        while (resultSet.next()) {
            String groupName = resultSet.getString(GROUP_NAME);
            int id = resultSet.getInt(GROUP_ID);

            groups.add(String.format("%d. %s", id, groupName));
        }

        return groups.toString();
    }
}
