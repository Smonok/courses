package com.foxminded.courses;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DataPrinterTest {
    private static final JdbcDataSource dataSource = new JdbcDataSource();
    private static final DataPrinter printer = new DataPrinter();
    private ResultSet resultSet;

    @BeforeAll
    static void setUpBeforeClass() throws SQLException {
        dataSource.setURL("jdbc:h2:mem:estest;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                TablesUtil.fillAllTables(statement);
            } catch (SQLException e) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Test
    void printGroupsAndStudentsNumberShouldSelectNumberOfStudentsLessThanEnteredWhenOne() throws SQLException {
        int studentsNumber = 25;
        int lessOrEquals = 1;

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                resultSet = printer.printGroupsAndStudentsNumber(statement, studentsNumber, lessOrEquals);

                while (resultSet.next()) {
                    int count = resultSet.getInt("count");

                    assertTrue(count < studentsNumber);
                }
            } catch (SQLException e) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Test
    void printGroupsAndStudentsNumberShouldSelectNumberOfStudentsEqualsToEnteredWhenTwo() throws SQLException {
        int studentsNumber = 23;
        int lessOrEquals = 2;

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                resultSet = printer.printGroupsAndStudentsNumber(statement, studentsNumber, lessOrEquals);

                while (resultSet.next()) {
                    int count = resultSet.getInt("count");

                    assertTrue(count == studentsNumber);
                }
            } catch (SQLException e) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Test
    void printStudentsRelatedToCourseShouldThrowSQLExceptionWhenCourseDoesntExist() throws SQLException {
        int courseId = 0;

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                assertThrows(SQLException.class, () -> printer.printStudentsRelatedToCourse(statement, courseId));
            } catch (SQLException e) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    @Test
    void printStudentsRelatedToCourseShouldReturnAllStudentsThatInMathCourseWhenIdOne() throws SQLException {
        int courseId = 1;

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                resultSet = printer.printStudentsRelatedToCourse(statement, courseId);

                while (resultSet.next()) {
                    int course = resultSet.getInt("course_id");
                    int studentId = resultSet.getInt("student_id");

                    assertTrue(course == courseId);
                    assertTrue(TablesUtil.isStudentExists(studentId));
                }
            } catch (SQLException e) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new SQLException();
        }
    }
}
