package com.foxminded.courses.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringJoiner;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.foxminded.courses.DatabaseConstants;
import com.foxminded.courses.TablesInitializer;

class StudentsDaoTest {
    private static final JdbcDataSource dataSource = new JdbcDataSource();
    private static final String STUDENTS_BY_MATH_COURSE = "SELECT students_courses.student_id, first_name, last_name\n"
                + "FROM students_courses\n"
                + "LEFT JOIN students ON students_courses.student_id = students.student_id\n"
                + "LEFT JOIN courses ON students_courses.course_id = courses.course_id\n"
                + "GROUP BY students_courses.student_id, courses.course_id, first_name, last_name\n"
                + "HAVING courses.course_id = 1;";
    private static final String SELECT_STUDENTS_ID = "SELECT student_id FROM students;";
    private static final String STUDENT_ID = "student_id";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static ResultSet resultSet;
    private static StudentsDao students;
    private static TablesInitializer initializer;

    @BeforeAll
    static void setUp() throws SQLException {
        dataSource.setURL("jdbc:h2:mem:estest;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");

        initializer = new TablesInitializer(dataSource);
        students = new StudentsDao(dataSource);

        initializer.initStudentsTable();
        initializer.initStudentsCoursesTable();
    }

    @Test
    void isStudentExistsShouldReturnTrueWhenIdFromOneToStudentsNumber() throws SQLException {
        for (int i = 1; i <= DatabaseConstants.STUDENTS_NUMBER; i++) {
            assertTrue(students.isStudentExists(i));
        }
    }

    @Test
    void addStudentShouldAddNewStudentWithIdOneMoreThanStudentsNumberBefore() throws SQLException {
        String firstName = "Arthur";
        String lastName = "Fleck";
        int groupId = 1;

        students.addStudent(firstName, lastName, groupId);
        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            int expectedLastId = DatabaseConstants.STUDENTS_NUMBER + 1;
            int actualLastId = 0;

            resultSet = statement.executeQuery(SELECT_STUDENTS_ID);
            while (resultSet.next()) {
                actualLastId = resultSet.getInt(STUDENT_ID);
            }

            assertEquals(expectedLastId, actualLastId);
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Test
    void deleteStudentByIdShouldDeleteFirstStudentWhenIdEqualsOne() throws SQLException {
        int studentId = 1;

        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            students.deleteStudentById(studentId);

            resultSet = statement.executeQuery(SELECT_STUDENTS_ID);
            resultSet.next();
            int actualFirstId = resultSet.getInt(STUDENT_ID);
            int expectedFirstId = 2;

            assertEquals(expectedFirstId, actualFirstId);
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Test
    void selectStudentsByCourseShouldThrowSQLExceptionWhenCourseDoesntExist() throws SQLException {
        int courseId = 0;

        assertThrows(SQLException.class, () -> students.selectStudentsByCourse(courseId));
    }

    @Test
    void selectStudentsByCourseShouldReturnAllStudentsThatInMathCourseWhenIdOne() throws SQLException {
        StringJoiner expectedResult = new StringJoiner("\n");
        int courseId = 1;
        String actualResult = students.selectStudentsByCourse(courseId);

        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery(STUDENTS_BY_MATH_COURSE);

            while (resultSet.next()) {
                int studentId = resultSet.getInt(STUDENT_ID);
                String firstName = resultSet.getString(FIRST_NAME);
                String lastName = resultSet.getString(LAST_NAME);

                expectedResult.add(String.format("%d. %s %s", studentId, firstName, lastName));
            }

            assertEquals(expectedResult.toString(), actualResult);
        } catch (SQLException e) {
            throw new SQLException();
        }
    }
}
