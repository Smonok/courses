package com.foxminded.courses.dao;

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
import java.util.StringJoiner;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.foxminded.courses.DatabaseConstants;
import com.foxminded.courses.TablesInitializer;

class CoursesDaoTest {
    private static final String FIRST_STUDENT_COURSES = "SELECT students_courses.course_id, course_name\n"
        + "FROM students_courses\n" + "LEFT JOIN courses ON students_courses.course_id = courses.course_id\n"
        + "GROUP BY students_courses.course_id, course_name,  students_courses.student_id\n"
        + "HAVING students_courses.student_id = 1;";
    private static final String STUDENT_COURSES_ID = "SELECT course_id FROM students_courses "
        + "WHERE student_id = %d;";
    private static final String COURSE_ID = "course_id";
    private static final String COURSE_NAME = "course_name";
    private static final JdbcDataSource dataSource = new JdbcDataSource();
    private static final List<Integer> firstStudentCourses = new ArrayList<>();
    private static CoursesDao courses;

    @BeforeAll
    static void setUp() throws SQLException {
        dataSource.setURL("jdbc:h2:mem:estest;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");

        TablesInitializer initializer = new TablesInitializer(dataSource);
        courses = new CoursesDao(dataSource);

        initializer.initTables();
        initFirstStudentCourses();
    }

    private static void initFirstStudentCourses() throws SQLException {
        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(selectStudentCourses(1))) {

            while (resultSet.next()) {
                int courseId = resultSet.getInt(COURSE_ID);
                firstStudentCourses.add(courseId);
            }
        }
    }

    private static String selectStudentCourses(int studentId) {
        return String.format(STUDENT_COURSES_ID, studentId);
    }

    @Test
    void isCourseExistsShouldReturnTrueWhenIdFromOneToCoursesNumber() throws SQLException {
        for (int i = 1; i <= DatabaseConstants.COURSES_NUMBER; i++) {
            assertTrue(courses.isCourseExists(i));
        }
    }

    @Test
    void selectCoursesByStudentIdShouldReturnAllCoursesThatFirstStudentHasWhenIdOne() throws SQLException {
        StringJoiner expectedResult = new StringJoiner("\n");
        int studentId = 1;
        String actualResult = courses.selectCoursesByStudentId(studentId);

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(FIRST_STUDENT_COURSES)) {

            while (resultSet.next()) {
                int courseId = resultSet.getInt(COURSE_ID);
                String name = resultSet.getString(COURSE_NAME);

                expectedResult.add(String.format("%d. %s", courseId, name));
            }

            assertEquals(expectedResult.toString(), actualResult);
        }
    }

    @Test
    void isStudentHasCourseShouldReturnTrueWhenFirstStudentRelatedEachCourseFromList() throws SQLException {
        int studentId = 1;

        for (int courseId : firstStudentCourses) {
            assertTrue(courses.isStudentHasCourse(studentId, courseId));
        }
    }

    @Test
    void addStudentToCourseShouldThrowSQLExceptionWhenStudentDoesntExist() {
        int courseId = 1;

        assertThrows(SQLException.class, () -> courses.addStudentToCourse(0, courseId));
        assertThrows(SQLException.class, () -> courses.addStudentToCourse(-11, courseId));
        assertThrows(SQLException.class, () -> courses.addStudentToCourse(333, courseId));
    }

    @Test
    void addStudentToCourseShouldThrowSQLExceptionWhenStudentAlreadyHasCourse() throws SQLException {
        int studentId = 2;

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(selectStudentCourses(studentId))) {

            resultSet.next();

            int existingCourseId = resultSet.getInt(COURSE_ID);

            assertThrows(SQLException.class, () -> courses.addStudentToCourse(studentId, existingCourseId));
        }
    }

    @Test
    void addStudentToCourseShouldAddFirstStudentToCourseThatHeDoesntHave() throws SQLException {
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

            courses.addStudentToCourse(studentId, courseId);
            try (ResultSet resultSet = statement.executeQuery(FIRST_STUDENT_COURSES)) {
                List<Integer> studentCourses = new ArrayList<>();

                while (resultSet.next()) {
                    studentCourses.add(resultSet.getInt(COURSE_ID));
                }

                assertTrue(studentCourses.contains(courseId));
            }
        }
    }

    @Test
    void removeStudentFromCourseShouldDeleteFirstCourseFromFirstStudentWhenIdsEqualsOne() throws SQLException {
        int studentId = 1;
        int courseToDelete = firstStudentCourses.get(0);

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()) {
            courses.removeStudentFromCourse(studentId, courseToDelete);

            try (ResultSet resultSet = statement.executeQuery(FIRST_STUDENT_COURSES)) {
                while (resultSet.next()) {
                    int courseId = resultSet.getInt(COURSE_ID);

                    assertNotEquals(courseToDelete, courseId);
                }
            }
        }
    }
}
