package com.foxminded.courses;

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

class DBDataSelectorTest {
    private static final String STUDENTS_BY_MATH_COURSE = "SELECT students_courses.student_id, first_name, last_name\n"
                + "FROM students_courses\n"
                + "LEFT JOIN students ON students_courses.student_id = students.student_id\n"
                + "LEFT JOIN courses ON students_courses.course_id = courses.course_id\n"
                + "GROUP BY students_courses.student_id, courses.course_id, first_name, last_name\n"
                + "HAVING courses.course_id = 1;";

    public static final String FIRST_STUDENT_COURSES = "SELECT students_courses.course_id, course_name\n"
                + "FROM students_courses\n" + "LEFT JOIN courses ON students_courses.course_id = courses.course_id\n"
                + "GROUP BY students_courses.course_id, course_name,  students_courses.student_id\n"
                + "HAVING students_courses.student_id = 1;";

    private static final JdbcDataSource dataSource = new JdbcDataSource();
    private static ResultSet resultSet;
    private static DBDataSelector selector;
    private static TablesFiller filler;

    @BeforeAll
    static void setUp() throws SQLException {
        dataSource.setURL("jdbc:h2:mem:estest;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");

        filler = new TablesFiller(dataSource);
        selector = new DBDataSelector(dataSource);

        filler.fillAllTables();
    }

    @Test
    void selectGroupsWithStudentsNumberShouldSelectNumberOfStudentsLessThanEnteredWhenOne() throws SQLException {
        int studentsNumber = 30;
        int lessOrEquals = 1;

        String actualResult = selector.selectGroupsWithStudentsNumber(studentsNumber, lessOrEquals);

        String[] resultLines = actualResult.split("[\\r\\n]+");

        for(String line: resultLines) {
            String[] groupWithStudents = line.split("\\s+");
            String students = groupWithStudents[groupWithStudents.length - 1];
            int count = Integer.parseInt(students);

            assertTrue(count < studentsNumber);
        }
    }

    @Test
    void selectGroupsWithStudentsNumberShouldReturnMessageWhenGroupsNotFound() throws SQLException {
        int studentsNumber = 3;
        int lessOrEquals = 1;
        final String expectedResult = "No groups found";

        String actualResult = selector.selectGroupsWithStudentsNumber(studentsNumber, lessOrEquals);
        assertEquals(expectedResult, actualResult);

        lessOrEquals = 2;
        actualResult = selector.selectGroupsWithStudentsNumber(studentsNumber, lessOrEquals);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void selectGroupsWithStudentsNumberShouldSelectNumberOfStudentsEqualsToEnteredWhenTwo() throws SQLException {
        int studentsNumber = 23;
        int lessOrEquals = 2;

        final int lastCharactersNumber = 2;

        String actualResult = selector.selectGroupsWithStudentsNumber(studentsNumber, lessOrEquals);

        if(!actualResult.equals("No groups found")) {
            String[] resultLines = actualResult.split("[\\r\\n]+");

            for(String line: resultLines) {
                String students = line.substring(line.length() - lastCharactersNumber);
                int count = Integer.parseInt(students);

                assertEquals(count, studentsNumber);
            }
        }
    }

    @Test
    void selectStudentsByCourseShouldThrowSQLExceptionWhenCourseDoesntExist() throws SQLException {
        int courseId = 0;

        assertThrows(SQLException.class, () -> selector.selectStudentsByCourse(courseId));
    }

    @Test
    void selectStudentsByCourseShouldReturnAllStudentsThatInMathCourseWhenIdOne() throws SQLException {
        StringJoiner expectedResult = new StringJoiner("\n");
        int courseId = 1;
        String actualResult = selector.selectStudentsByCourse(courseId);

        try (Connection connection = dataSource.getConnection();
                        Statement statement = connection.createStatement()) {
                resultSet = statement.executeQuery(STUDENTS_BY_MATH_COURSE);

                while (resultSet.next()) {
                    int studentId = resultSet.getInt("student_id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");

                    expectedResult.add(String.format("%d. %s %s", studentId, firstName, lastName));
                }

                assertEquals(expectedResult.toString(), actualResult);
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Test
    void selectCoursesByStudentIdShouldReturnAllCoursesThatFirstStudentHasWhenIdOne() throws SQLException {
        StringJoiner expectedResult = new StringJoiner("\n");
        int studentId = 1;
        String actualResult = selector.selectCoursesByStudentId(studentId);

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery(FIRST_STUDENT_COURSES);

            while (resultSet.next()) {
                int courseId = resultSet.getInt("course_id");
                String name = resultSet.getString("course_name");

                expectedResult.add(String.format("%d. %s", courseId, name));
            }

            assertEquals(expectedResult.toString(), actualResult);
        } catch (SQLException e) {
            throw new SQLException();
        }
    }
}
