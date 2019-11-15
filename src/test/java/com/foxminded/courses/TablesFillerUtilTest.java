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

class TablesFillerUtilTest {
    private static final JdbcDataSource dataSource = new JdbcDataSource();
    private static final String TWO_UPPER_CASE_LETTERS = "[A-Z]{2}";
    private static final String HYPHEN_AND_TWO_DIGITS = "\\-\\d{2}";

    private static final String SELECT_GROUP_NAME = "SELECT group_name FROM groups;";

    private static final String SELECT_GROUPS_NUMBER = "SELECT COUNT (DISTINCT group_id) AS count FROM groups;";
    private static final String SELECT_COURSES_NUMBER = "SELECT COUNT (DISTINCT course_id) AS count FROM courses;";
    private static final String SELECT_STUDENTS_NUMBER = "SELECT COUNT (DISTINCT student_id) AS count FROM students;";
    private static final String SELECT_SORTED_COURSES_NUMBER = "SELECT student_id, COUNT(student_id) AS courses_count\n"
                    + "FROM students_courses\n" + "GROUP BY student_id\n" + "ORDER BY courses_count DESC;";
    private ResultSet resultSet;

    @BeforeAll
    static void setUpBeforeClass() throws SQLException {
        dataSource.setURL("jdbc:h2:mem:estest;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
    }

    @Test
    void fillGroupsTableShouldCreateTenGroups() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                TablesUtil.fillGroupsTable(statement);

                resultSet = statement.executeQuery(SELECT_GROUPS_NUMBER);
                resultSet.next();
                int count = resultSet.getInt("count");
                int expectedResult = 10;

                assertEquals(expectedResult, count);
            } catch (SQLException e) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Test
    void fillGroupsTableShouldCreateGroupNameWhereFirstTwoCharactersAreUpperCaseLetters() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                TablesUtil.fillGroupsTable(statement);

                resultSet = statement.executeQuery(SELECT_GROUP_NAME);
                resultSet.next();
                String groupName = resultSet.getString("group_name");
                int firstCharactersNumber = 2;
                String firstCharacters = groupName.substring(0, firstCharactersNumber);

                assertTrue(firstCharacters.matches(TWO_UPPER_CASE_LETTERS));
            } catch (SQLException e) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Test
    void fillGroupsTableShouldCreateGroupNameWhereLastCharactestAreHyphenAndTwoDigits() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                TablesUtil.fillGroupsTable(statement);

                resultSet = statement.executeQuery(SELECT_GROUP_NAME);
                resultSet.next();
                String groupName = resultSet.getString("group_name");
                int hyphenIndex = 2;
                String lastCharacters = groupName.substring(hyphenIndex);

                assertTrue(lastCharacters.matches(HYPHEN_AND_TWO_DIGITS));
            } catch (SQLException e) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Test
    void isGroupExistsInDBShouldReturnTrueWhenIdFromOneToGroupsNumber() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                TablesUtil.fillGroupsTable(statement);

                for (int i = 1; i <= DatabaseConstants.GROUPS_NUMBER; i++) {
                    assertTrue(TablesUtil.isGroupExists(i));
                }
            } catch (SQLException e) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Test
    void fillCoursesTableShouldCreateTenCourses() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                TablesUtil.fillCoursesTable(statement);

                resultSet = statement.executeQuery(SELECT_COURSES_NUMBER);
                resultSet.next();
                int count = resultSet.getInt("count");
                int expectedResult = 10;

                assertEquals(expectedResult, count);
            } catch (SQLException e) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Test
    void isCourseExistsInDBShouldReturnTrueWhenIdFromOneToCoursesNumber() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                TablesUtil.fillCoursesTable(statement);

                for (int i = 1; i <= DatabaseConstants.COURSES_NUMBER; i++) {
                    assertTrue(TablesUtil.isCourseExists(i));
                }
            } catch (SQLException e) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Test
    void fillStudentsTableShouldCreateTwoHundredStudents() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                TablesUtil.fillStudentsTable(statement);

                resultSet = statement.executeQuery(SELECT_STUDENTS_NUMBER);
                resultSet.next();
                int count = resultSet.getInt("count");
                int expectedResult = 200;

                assertEquals(expectedResult, count);
            } catch (SQLException e) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Test
    void isStudentExistsInDBShouldReturnTrueWhenIdFromOneToStudentsNumber() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                TablesUtil.fillStudentsTable(statement);

                for (int i = 1; i <= DatabaseConstants.STUDENTS_NUMBER; i++) {
                    assertTrue(TablesUtil.isStudentExists(i));
                }
            } catch (SQLException e) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Test
    void fillStudentsCoursesTableShouldAssignForEachStudentFromOneToThreeCourses() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                TablesUtil.fillStudentsTable(statement);
                TablesUtil.fillCoursesTable(statement);
                TablesUtil.fillStudentsCoursesTable(statement);

                resultSet = statement.executeQuery(SELECT_SORTED_COURSES_NUMBER);
                resultSet.next();
                int maxCoursesCount = resultSet.getInt("courses_count");
                int minCoursesCount = 0;

                while (resultSet.next()) {
                    minCoursesCount = resultSet.getInt("courses_count");
                }

                assertTrue(minCoursesCount >= 1);
                assertTrue(maxCoursesCount <= 3);
            } catch (SQLException e) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
    }
}
