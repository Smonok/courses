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

import javax.naming.NamingException;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class StudentsControllerTest {
    private static Connection connection = null;
    private static Statement statement = null;
    private static ResultSet resultSet;
    private final StudentsController controller = new StudentsController();
    private final String STUDENTS_ID = "SELECT student_id FROM students;";
    private final static String FIRST_STUDENT_COURSES = "SELECT course_id FROM students_courses WHERE student_id = 1;";
    private final int WRONG_ID = 0;
    private final static List<Integer> firstStudentCourses = new ArrayList<>();

    @BeforeAll
    static void connectToDatabase() throws NamingException, SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:estest;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        connection = dataSource.getConnection();
        statement = connection.createStatement();

        TablesFillerUtil.fillAllTables(statement);
        fillFirstStudentCourses();
    }

    private static void fillFirstStudentCourses() throws SQLException {
        resultSet = statement.executeQuery(FIRST_STUDENT_COURSES);
        while (resultSet.next()) {
            firstStudentCourses.add(resultSet.getInt("course_id"));
        }
    }

    @AfterAll
    static void close() throws SQLException {
        connection.close();
        statement.close();
    }

    @Test
    void printGroupsAndStudentsNumberShouldSelectNumberOfStudentsLessThanEnteredWhenOne() throws SQLException {
        int studentsNumber = 25;
        int lessOrEquals = 1;

        resultSet = controller.printGroupsAndStudentsNumber(statement, studentsNumber, lessOrEquals);

        while (resultSet.next()) {
            int count = resultSet.getInt("count");

            assertTrue(count < studentsNumber);
        }
    }

    @Test
    void printGroupsAndStudentsNumberShouldSelectNumberOfStudentsEqualsToEnteredWhenTwo() throws SQLException {
        int studentsNumber = 23;
        int lessOrEquals = 2;

        resultSet = controller.printGroupsAndStudentsNumber(statement, studentsNumber, lessOrEquals);

        while (resultSet.next()) {
            int count = resultSet.getInt("count");

            assertTrue(count == studentsNumber);
        }
    }

    @Test
    void printStudentsRelatedToCourseShouldThrowSQLExceptionWhenCourseDoesntExist() throws SQLException {
        int courseId = 0;

        assertThrows(SQLException.class, () -> controller.printStudentsRelatedToCourse(statement, courseId));
    }

    @Test
    void printStudentsRelatedToCourseShouldReturnAllStudentsThatInMathCourseWhenIdOne() throws SQLException {
        int courseId = 1;
        resultSet = controller.printStudentsRelatedToCourse(statement, courseId);

        while (resultSet.next()) {
            int course = resultSet.getInt("course_id");
            int studentId = resultSet.getInt("student_id");

            assertTrue(course == courseId);
            assertTrue(TablesFillerUtil.doesStudentExists(studentId));
        }
    }

    @Test
    void addStudentShouldAddNewStudentWithIdOneMoreThanStudentsNumberBefore() throws SQLException {
        String firstName = "Arthur";
        String lastName = "Fleck";
        int groupId = 1;
        controller.addStudent(statement, firstName, lastName, groupId);

        int expectedLastId = DatabaseConstants.STUDENTS_NUMBER + 1;
        int actualLastId = 0;

        resultSet = statement.executeQuery(STUDENTS_ID);
        while (resultSet.next()) {
            if (resultSet.isLast()) {
                actualLastId = resultSet.getInt("student_id");
            }
        }

        assertEquals(expectedLastId, actualLastId);
    }

    @Test
    void deleteStudentByIdShouldDeleteFirstStudentWhenIdEqualsOne() throws SQLException {
        int studentId = 1;
        controller.deleteStudentById(statement, studentId);

        resultSet = statement.executeQuery(STUDENTS_ID);
        resultSet.next();
        int actualFirstId = resultSet.getInt("student_id");
        int expectedFirstId = 2;

        assertEquals(expectedFirstId, actualFirstId);
    }

    @Test
    void addCourseToStudentShouldThrowSQLExceptionWhenStudentDoesntExist() throws SQLException {
        int courseId = 1;
        assertThrows(SQLException.class, () -> controller.addCourseToStudent(statement, WRONG_ID, courseId));
    }

    @Test
    void addCourseToStudentShouldThrowSQLExceptionWhenStudentAlreadyHasCourse() throws SQLException {
        int studentId = 1;

        for (int i = 1; i <= DatabaseConstants.COURSES_NUMBER; i++) {
            if (firstStudentCourses.contains(i)) {
                final int EXISTING_COURSE_ID = i;

                assertThrows(SQLException.class, () -> controller.addCourseToStudent(statement, studentId,
                                EXISTING_COURSE_ID));
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
        controller.addCourseToStudent(statement, studentId, courseId);

        List<Integer> studentCourses = new ArrayList<>();

        resultSet = statement.executeQuery(FIRST_STUDENT_COURSES);
        while (resultSet.next()) {
            studentCourses.add(resultSet.getInt("course_id"));
        }

        assertTrue(studentCourses.contains(courseId));
    }

    void removeStudentFromCourseShouldThrowSQLExceptionWhenStudentOrCourseDoesntExist() throws SQLException {
        int courseId = 1;
        assertThrows(SQLException.class, () -> controller.removeStudentFromCourse(statement, WRONG_ID, courseId));

        int studentId = 1;
        assertThrows(SQLException.class, () -> controller.removeStudentFromCourse(statement, studentId, WRONG_ID));
    }

    void removeStudentFromCourseShouldDeleteFirstCourseFromFirstStudentWhenIdsEqualsOne() throws SQLException {
        int studentId = 1;
        int courseId = 1;
        controller.removeStudentFromCourse(statement, studentId, courseId);

        assertTrue(!firstStudentCourses.contains(courseId));
    }
}
