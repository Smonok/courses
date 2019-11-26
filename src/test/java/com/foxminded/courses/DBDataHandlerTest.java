package com.foxminded.courses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DBDataHandlerTest {
    private static final JdbcDataSource dataSource = new JdbcDataSource();
    private static final String STUDENTS_ID = "SELECT student_id FROM students;";
    private static final String FIRST_STUDENT_COURSES = "SELECT course_id FROM students_courses WHERE student_id = 1;";
    private static final String COURSE_ID = "course_id";
    private static final String STUDENT_ID = "student_id";
    private static final List<Integer> firstStudentCourses = new ArrayList<>();
    private static TablesFiller filler;
    private static DBDataHandler handler;
    private static ResultSet resultSet;

    @BeforeAll
    static void setUp() throws SQLException {
        dataSource.setURL("jdbc:h2:mem:estest;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");

        filler = new TablesFiller(dataSource);
        handler = new DBDataHandler(dataSource);

        filler.fillAllTables();
        try (Connection connection = dataSource.getConnection();
                        Statement statement = connection.createStatement()) {
                fillFirstStudentCourses(statement);
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    private static void fillFirstStudentCourses(Statement statement) throws SQLException {
        resultSet = statement.executeQuery(FIRST_STUDENT_COURSES);
        while (resultSet.next()) {
            firstStudentCourses.add(resultSet.getInt(COURSE_ID));
        }
    }

    @Test
    void addStudentShouldAddNewStudentWithIdOneMoreThanStudentsNumberBefore() throws SQLException {
        String firstName = "Arthur";
        String lastName = "Fleck";
        int groupId = 1;

        handler.addStudent(firstName, lastName, groupId);
        try (Connection connection = dataSource.getConnection();
                        Statement statement = connection.createStatement()) {
                int expectedLastId = DatabaseConstants.STUDENTS_NUMBER + 1;
                int actualLastId = 0;

                resultSet = statement.executeQuery(STUDENTS_ID);
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
                handler.deleteStudentById(studentId);

                resultSet = statement.executeQuery(STUDENTS_ID);
                resultSet.next();
                int actualFirstId = resultSet.getInt(STUDENT_ID);
                int expectedFirstId = 2;

                assertEquals(expectedFirstId, actualFirstId);
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Test
    void addCourseToStudentShouldThrowSQLExceptionWhenStudentDoesntExist() throws SQLException {
        int courseId = 1;

        assertThrows(SQLException.class, () -> handler.addCourseToStudent(0, courseId));
        assertThrows(SQLException.class, () -> handler.addCourseToStudent(-11, courseId));
        assertThrows(SQLException.class, () -> handler.addCourseToStudent(333, courseId));
    }

    @Test
    void addCourseToStudentShouldThrowSQLExceptionWhenStudentAlreadyHasCourse() throws SQLException {
        int studentId = 1;

        for (int i = 1; i <= DatabaseConstants.COURSES_NUMBER; i++) {
            if (firstStudentCourses.contains(i)) {
                final int EXISTING_COURSE_ID = i;

                assertThrows(SQLException.class, () -> handler.addCourseToStudent(studentId, EXISTING_COURSE_ID));
                break;
            }
        }
    }

    @Test
    void addCourseToStudentShouldAddFirstStudentToCourseThatHeDoesntHave() throws SQLException {
        int studentId = 1;
        int courseId = 0;

        for (int i = 1; i <= DatabaseConstants.COURSES_NUMBER; i++) {
            if (!firstStudentCourses.contains(i)) {
                courseId = i;
                break;
            }
        }

        try (Connection connection = dataSource.getConnection();
                        Statement statement = connection.createStatement()) {
                handler.addCourseToStudent(studentId, courseId);

                List<Integer> studentCourses = new ArrayList<>();

                resultSet = statement.executeQuery(FIRST_STUDENT_COURSES);
                while (resultSet.next()) {
                    studentCourses.add(resultSet.getInt(COURSE_ID));
                }

                assertTrue(studentCourses.contains(courseId));
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Test
    void removeStudentFromCourseShouldDeleteFirstCourseFromFirstStudentWhenIdsEqualsOne() throws SQLException {
        int studentId = 1;
        int courseToDelete = firstStudentCourses.get(0);

        try (Connection connection = dataSource.getConnection();
                        Statement statement = connection.createStatement()) {
                handler.removeStudentFromCourse(studentId, courseToDelete);

                resultSet = statement.executeQuery(FIRST_STUDENT_COURSES);
                while (resultSet.next()) {
                    int courseId = resultSet.getInt(COURSE_ID);

                    assertNotEquals(courseToDelete, courseId);
                }
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Test
    void isStudentExistsShouldReturnTrueWhenIdFromOneToStudentsNumber() throws SQLException {
        for (int i = 1; i <= DatabaseConstants.STUDENTS_NUMBER; i++) {
            assertTrue(handler.isStudentExists(i));
        }
    }

    @Test
    void isCourseExistsShouldReturnTrueWhenIdFromOneToCoursesNumber() throws SQLException {
        for (int i = 1; i <= DatabaseConstants.COURSES_NUMBER; i++) {
            assertTrue(handler.isCourseExists(i));
        }
    }
}
