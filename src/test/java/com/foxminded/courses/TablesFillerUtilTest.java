package com.foxminded.courses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.NamingException;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TablesFillerUtilTest {
    private static Connection connection = null;
    private static Statement statement = null;
    private ResultSet resultSet;
    private final String TWO_UPPER_CASE_LETTERS = "[A-Z]{2}";
    private final String HYPHEN_AND_TWO_DIGITS = "\\-\\d{2}";

    private final String SELECT_GROUP_NAME = "SELECT group_name FROM groups;";

    private final String SELECT_GROUPS_NUMBER = "SELECT COUNT (DISTINCT group_id) AS count FROM groups;";
    private final String SELECT_COURSES_NUMBER = "SELECT COUNT (DISTINCT course_id) AS count FROM courses;";
    private final String SELECT_STUDENTS_NUMBER = "SELECT COUNT (DISTINCT student_id) AS count FROM students;";
    private final String SELECT_SORTED_COURSES_NUMBER = "SELECT student_id, COUNT(student_id) AS courses_count\n"
                    + "FROM students_courses\n" + "GROUP BY student_id\n" + "ORDER BY courses_count DESC;";

    @BeforeAll
    static void connectToDatabase() throws NamingException, SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:estest;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        connection = dataSource.getConnection();
        statement = connection.createStatement();
    }

    @AfterAll
    static void close() throws SQLException {
        connection.close();
        statement.close();
    }

    @Test
    void fillGroupsTableShouldCreateTenGroups() throws SQLException {
        TablesFillerUtil.fillGroupsTable(statement);

        resultSet = statement.executeQuery(SELECT_GROUPS_NUMBER);
        resultSet.next();
        int count = resultSet.getInt("count");
        int expectedResult = 10;

        assertEquals(expectedResult, count);
    }

    @Test
    void fillGroupsTableShouldCreateGroupNameWhereFirstTwoCharactersAreUpperCaseLetters() throws SQLException {
        TablesFillerUtil.fillGroupsTable(statement);

        resultSet = statement.executeQuery(SELECT_GROUP_NAME);
        resultSet.next();
        String groupName = resultSet.getString("group_name");
        int firstCharactersNumber = 2;
        String firstCharacters = groupName.substring(0, firstCharactersNumber);

        assertTrue(firstCharacters.matches(TWO_UPPER_CASE_LETTERS));
    }

    @Test
    void fillGroupsTableShouldCreateGroupNameWhereLastCharactestAreHyphenAndTwoDigits() throws SQLException {
        TablesFillerUtil.fillGroupsTable(statement);

        resultSet = statement.executeQuery(SELECT_GROUP_NAME);
        resultSet.next();
        String groupName = resultSet.getString("group_name");
        int hyphenIndex = 2;
        String lastCharacters = groupName.substring(hyphenIndex);

        assertTrue(lastCharacters.matches(HYPHEN_AND_TWO_DIGITS));
    }

    @Test
    void doesGroupExistsInDBShouldReturnTrueWhenIdFromOneToGroupsNumber() throws SQLException {
        TablesFillerUtil.fillGroupsTable(statement);

        for (int i = 1; i <= DatabaseConstants.GROUPS_NUMBER; i++) {
            assertTrue(TablesFillerUtil.doesGroupExists(i));
        }
    }

    @Test
    void fillCoursesTableShouldCreateTenCourses() throws SQLException {
        TablesFillerUtil.fillCoursesTable(statement);

        resultSet = statement.executeQuery(SELECT_COURSES_NUMBER);
        resultSet.next();
        int count = resultSet.getInt("count");
        int expectedResult = 10;

        assertEquals(expectedResult, count);
    }

    @Test
    void doesCourseExistsInDBShouldReturnTrueWhenIdFromOneToCoursesNumber() throws SQLException {
        TablesFillerUtil.fillCoursesTable(statement);

        for (int i = 1; i <= DatabaseConstants.COURSES_NUMBER; i++) {
            assertTrue(TablesFillerUtil.doesCourseExists(i));
        }
    }

    @Test
    void fillStudentsTableShouldCreateTwoHundredStudents() throws SQLException {
        TablesFillerUtil.fillStudentsTable(statement);

        resultSet = statement.executeQuery(SELECT_STUDENTS_NUMBER);
        resultSet.next();
        int count = resultSet.getInt("count");
        int expectedResult = 200;

        assertEquals(expectedResult, count);
    }

    @Test
    void doesStudentExistsInDBShouldReturnTrueWhenIdFromOneToStudentsNumber() throws SQLException {
        TablesFillerUtil.fillStudentsTable(statement);

        for (int i = 1; i <= DatabaseConstants.STUDENTS_NUMBER; i++) {
            assertTrue(TablesFillerUtil.doesStudentExists(i));
        }
    }

    @Test
    void fillStudentsCoursesTableShouldAssignForEachStudentFromOneToThreeCourses() throws SQLException {
        TablesFillerUtil.fillStudentsTable(statement);
        TablesFillerUtil.fillCoursesTable(statement);
        TablesFillerUtil.fillStudentsCoursesTable(statement);

        resultSet = statement.executeQuery(SELECT_SORTED_COURSES_NUMBER);
        resultSet.next();
        int maxCoursesCount = resultSet.getInt("courses_count");
        int minCoursesCount = 0;

        while (resultSet.next()) {
            if (resultSet.isLast()) {
                minCoursesCount = resultSet.getInt("courses_count");
            }
        }

        assertTrue(minCoursesCount >= 1);
        assertTrue(maxCoursesCount <= 3);
    }
}
