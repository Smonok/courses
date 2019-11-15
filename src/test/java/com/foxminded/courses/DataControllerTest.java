package com.foxminded.courses;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

class DataControllerTest {
    private static final DataController controller = new DataController();
    private static final JdbcDataSource dataSource = new JdbcDataSource();
    private static final String SELECT_STUDENTS_ID = "SELECT student_id FROM students;";
    private static final String SELECT_FIRST_STUDENT_COURSES = "SELECT course_id FROM students_courses "
                    + "WHERE student_id = 1;";
    private static final String COURSE_ID = "course_id";
    private static final String STUDENT_ID = "student_id";
    private static final int WRONG_ID = 0;
    private static final List<Integer> firstStudentCourses = new ArrayList<>();
    private static ResultSet resultSet;

    @BeforeAll
    static void setUpBeforeClass() throws SQLException {
        dataSource.setURL("jdbc:h2:mem:estest;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                TablesUtil.fillAllTables(statement);
                fillFirstStudentCourses(statement);
            } catch (SQLException e) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    private static void fillFirstStudentCourses(Statement statement) throws SQLException {
        resultSet = statement.executeQuery(SELECT_FIRST_STUDENT_COURSES);
        while (resultSet.next()) {
            firstStudentCourses.add(resultSet.getInt(COURSE_ID));
        }
    }

    @Test
    void addStudentShouldAddNewStudentWithIdOneMoreThanStudentsNumberBefore() throws SQLException {
        String firstName = "Arthur";
        String lastName = "Fleck";
        int groupId = 1;

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                controller.addStudent(statement, firstName, lastName, groupId);

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
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Test
    void deleteStudentByIdShouldDeleteFirstStudentWhenIdEqualsOne() throws SQLException {
        int studentId = 1;

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                controller.deleteStudentById(statement, studentId);

                resultSet = statement.executeQuery(SELECT_STUDENTS_ID);
                resultSet.next();
                int actualFirstId = resultSet.getInt(STUDENT_ID);
                int expectedFirstId = 2;

                assertEquals(expectedFirstId, actualFirstId);
            } catch (SQLException e) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Test
    void addCourseToStudentShouldThrowSQLExceptionWhenStudentDoesntExist() throws SQLException {
        int courseId = 1;
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                assertThrows(SQLException.class, () -> controller.addCourseToStudent(statement, WRONG_ID, courseId));
            } catch (SQLException e) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Test
    void addCourseToStudentShouldThrowSQLExceptionWhenStudentAlreadyHasCourse() throws SQLException {
        int studentId = 1;

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                for (int i = 1; i <= DatabaseConstants.COURSES_NUMBER; i++) {
                    if (firstStudentCourses.contains(i)) {
                        final int EXISTING_COURSE_ID = i;

                        assertThrows(SQLException.class, () -> controller.addCourseToStudent(statement, studentId,
                                        EXISTING_COURSE_ID));
                        break;
                    }
                }
            } catch (SQLException e) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new SQLException();
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

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                controller.addCourseToStudent(statement, studentId, courseId);

                List<Integer> studentCourses = new ArrayList<>();

                resultSet = statement.executeQuery(SELECT_FIRST_STUDENT_COURSES);
                while (resultSet.next()) {
                    studentCourses.add(resultSet.getInt(COURSE_ID));
                }

                assertTrue(studentCourses.contains(courseId));

            } catch (SQLException e) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    void removeStudentFromCourseShouldDeleteFirstCourseFromFirstStudentWhenIdsEqualsOne() throws SQLException {
        int studentId = 1;
        int courseToDelete = firstStudentCourses.get(0);

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                controller.removeStudentFromCourse(statement, studentId, courseToDelete);

                resultSet = statement.executeQuery(SELECT_FIRST_STUDENT_COURSES);
                while (resultSet.next()) {
                    int courseId = resultSet.getInt(COURSE_ID);
                    assertTrue(courseToDelete != courseId);
                }
            } catch (SQLException e) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
    }
}
