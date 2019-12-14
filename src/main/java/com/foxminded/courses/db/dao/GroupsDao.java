package com.foxminded.courses.db.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringJoiner;

import javax.sql.DataSource;

import com.foxminded.courses.constants.DatabaseConstants;

public class GroupsDao {
    private static final String ERROR_MESSAGE = "No groups found";
    private static final String GROUP_ID = "group_id";
    private static final String GROUP_NAME = "group_name";
    private static final String COUNT = "count";
    private final DataSource dataSource;

    public GroupsDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String selectGroupsByStudentsNumber(int studentsNumber, int lessOrEquals) throws SQLException {
        String groupsWithStudentsCount = selectGroupsByStudentsCountQuery(studentsNumber, lessOrEquals);

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(groupsWithStudentsCount)) {

            return combineGroupsWithStudentsNumber(resultSet);
        } catch (SQLException e) {
            throw new SQLException("Cannot find all groups with " + studentsNumber + " students.: "
                + groupsWithStudentsCount, e);
        }
    }

    private String selectGroupsByStudentsCountQuery(int studentsNumber, int input) {
        String groupsWithStudentsCount = "";

        if (input == 1) {
            groupsWithStudentsCount = String.format(DatabaseConstants.GROUPS_WITH_STUDENTS_COUNT, '<', studentsNumber);
        } else if (input == 2) {
            groupsWithStudentsCount = String.format(DatabaseConstants.GROUPS_WITH_STUDENTS_COUNT, '=', studentsNumber);
        }

        return groupsWithStudentsCount;
    }

    private String combineGroupsWithStudentsNumber(ResultSet resultSet) throws SQLException {
        StringJoiner groupsWithStudents = new StringJoiner(": ");

        while (resultSet.next()) {
            String groupInfo = parseGroupInfo(resultSet);
            int studentsNumber = parseStudentsNumber(resultSet);
            String combinedInfo = String.format("%s <==> %d", groupInfo, studentsNumber);

            groupsWithStudents.add(combinedInfo);
        }

        if (groupsWithStudents.toString().isEmpty()) {
            return ERROR_MESSAGE;
        }

        return groupsWithStudents.toString();
    }

    private int parseStudentsNumber(ResultSet resultSet) throws SQLException{
        return resultSet.getInt(COUNT);
    }

    public String selectAllGroups() throws SQLException {
        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(DatabaseConstants.ALL_GROUPS)) {

            return combineGroupsInfo(resultSet);
        } catch (SQLException e) {
            throw new SQLException("Cannot select all groups.: " + DatabaseConstants.ALL_GROUPS, e);
        }
    }

    private String combineGroupsInfo(ResultSet resultSet) throws SQLException {
        StringJoiner groups = new StringJoiner(": ");

        while (resultSet.next()) {
            groups.add(parseGroupInfo(resultSet));
        }

        return groups.toString();
    }

    private String parseGroupInfo(ResultSet resultSet) throws SQLException {
        String groupName = resultSet.getString(GROUP_NAME);
        int id = resultSet.getInt(GROUP_ID);

        return String.format("%d. %s", id, groupName);
    }
}
