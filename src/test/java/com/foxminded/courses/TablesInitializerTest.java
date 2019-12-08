package com.foxminded.courses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TablesInitializerTest {
    private static final JdbcDataSource dataSource = new JdbcDataSource();
    private static final String TWO_UPPER_CASE_LETTERS = "[A-Z]{2}";
    private static final String HYPHEN_AND_TWO_DIGITS = "\\-\\d{2}";

    private static final String COUNT = "count";
    private static final String COURSES_COUNT = "courses_count";
    private static final String GROUP_NAME = "group_name";

    private static final String SELECT_GROUP_NAME = "SELECT group_name FROM groups;";

    private static final String SELECT_GROUPS_NUMBER = "SELECT COUNT (DISTINCT group_id) AS count FROM groups;";
    private static final String SELECT_COURSES_NUMBER = "SELECT COUNT (DISTINCT course_id) AS count FROM courses;";
    private static final String SELECT_STUDENTS_NUMBER = "SELECT COUNT (DISTINCT student_id) AS count FROM students;";
    private static final String SELECT_SORTED_COURSES_NUMBER = "SELECT student_id, COUNT(student_id) AS courses_count\n"
                    + "FROM students_courses\n" + "GROUP BY student_id\n" + "ORDER BY courses_count DESC;";
    private static TablesInitializer initializer;

    @BeforeAll
    static void setUp() throws SQLException {
        dataSource.setURL("jdbc:h2:mem:estest;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");

        initializer = new TablesInitializer(dataSource);
    }

    @Test
    void initGroupsTableShouldCreateTenGroups() throws SQLException {
        initializer.initGroupsTable();

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_GROUPS_NUMBER)) {

            resultSet.next();
            int count = resultSet.getInt(COUNT);
            int expectedResult = 10;

            assertEquals(expectedResult, count);
        }
    }

    @Test
    void initGroupsTableShouldCreateGroupNameWhereFirstTwoCharactersAreUpperCaseLetters() throws SQLException {
        initializer.initGroupsTable();

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_GROUP_NAME)) {

            resultSet.next();
            String groupName = resultSet.getString(GROUP_NAME);
            int firstCharactersNumber = 2;
            String firstCharacters = groupName.substring(0, firstCharactersNumber);

            assertTrue(firstCharacters.matches(TWO_UPPER_CASE_LETTERS));
        }
    }

    @Test
    void initGroupsTableShouldCreateGroupNameWhereLastCharactersAreHyphenAndTwoDigits() throws SQLException {
        initializer.initGroupsTable();

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_GROUP_NAME)) {

            resultSet.next();
            String groupName = resultSet.getString(GROUP_NAME);
            int hyphenIndex = 2;
            String lastCharacters = groupName.substring(hyphenIndex);

            assertTrue(lastCharacters.matches(HYPHEN_AND_TWO_DIGITS));
        }
    }

    @Test
    void initCoursesTableShouldCreateTenCourses() throws SQLException {
        initializer.initCoursesTable();

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_COURSES_NUMBER)) {

            resultSet.next();
            int count = resultSet.getInt(COUNT);
            int expectedResult = 10;

            assertEquals(expectedResult, count);
        }
    }

    @Test
    void initStudentsTableShouldCreateTwoHundredStudents() throws SQLException {
        initializer.initStudentsTable();

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_STUDENTS_NUMBER)) {

            resultSet.next();
            int count = resultSet.getInt(COUNT);
            int expectedResult = 200;

            assertEquals(expectedResult, count);
        }
    }

    @Test
    void initStudentsCoursesTableShouldAssignForEachStudentFromOneToThreeCourses() throws SQLException {
        initializer.initStudentsTable();
        initializer.initCoursesTable();
        initializer.initStudentsCoursesTable();

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_SORTED_COURSES_NUMBER)) {

            resultSet.next();
            int maxCoursesCount = 3;
            int minCoursesCount = 1;
            int actualCoursesCount = 0;

            while (resultSet.next()) {
                actualCoursesCount = resultSet.getInt(COURSES_COUNT);
            }

            assertTrue(minCoursesCount <= actualCoursesCount);
            assertTrue(maxCoursesCount >= actualCoursesCount);
        }
    }
}
