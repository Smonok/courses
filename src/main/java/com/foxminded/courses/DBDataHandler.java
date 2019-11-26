package com.foxminded.courses;

import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;

public class DBDataHandler {
    private static final String EXISTS = "is_exists";
    private static final Logger LOG = getLogger(DBDataHandler.class);
    private final DataSource dataSource;
    private ResultSet resultSet;

    public DBDataHandler(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isStudentExists(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery(String.format(DatabaseConstants.IS_STUDENT_EXISTS, id));

            resultSet.next();
            return resultSet.getBoolean(EXISTS);
        } catch (SQLException e) {
            throw new SQLException("Cannot check if student exists", e);
        }
    }

    public boolean isCourseExists(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery(String.format(DatabaseConstants.IS_COURSE_EXISTS, id));

            resultSet.next();
            return resultSet.getBoolean(EXISTS);
        } catch (SQLException e) {
            throw new SQLException("Cannot check if course exists", e);
        }
    }

    public boolean isStudentHasCourse(int studentId, int courseId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery(String.format(DatabaseConstants.IS_STUDENT_COURSE_EXISTS,
                            studentId, courseId));

            resultSet.next();
            return resultSet.getBoolean(EXISTS);
        } catch (SQLException e) {
            throw new SQLException("Cannot check if student has course", e);
        }
    }

    public void addStudent(String firstName, String lastName, int groupId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            int studentId = DatabaseConstants.STUDENTS_NUMBER;

            statement.executeUpdate(String.format(DatabaseConstants.INSERT_STUDENT, ++studentId, firstName, lastName,
                            groupId));
            LOG.info("Student added successfully");
        } catch (SQLException e) {
            throw new SQLException("Cannot add student: " + firstName + " " + lastName + ", group ID = " + groupId, e);
        }
    }

    public void deleteStudentById(int studentId) throws SQLException {
        try (Connection connection =  dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            String deleteStudent = String.format(DatabaseConstants.DELETE_STUDENT_BY_ID, studentId);

            statement.executeUpdate(deleteStudent);
            LOG.info("Student successfully deleted");
        } catch (SQLException e) {
            throw new SQLException("Cannot delete student with ID = " + studentId, e);
        }
    }

    public void addCourseToStudent(int studentId, int courseId) throws SQLException {
        try (Connection connection =  dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            String addCourseForStudent = String.format(DatabaseConstants.INSERT_STUDENT_COURSE, studentId, courseId);

            statement.executeUpdate(addCourseForStudent);
            LOG.info("Course added successfully");
        } catch (SQLException e) {
            throw new SQLException("Cannot add student with ID = " + studentId + " to " + courseId + " course", e);
        }
    }

    public void removeStudentFromCourse(int studentId, int courseId) throws SQLException {
        try (Connection connection =  dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            String deleteStudentCourse = String.format(DatabaseConstants.DELETE_STUDENT_FROM_COURSE, studentId,
                            courseId);

            statement.executeUpdate(deleteStudentCourse);
            LOG.info("Course deleted successfully");
        } catch (SQLException e) {
            throw new SQLException("Cannot remove student with ID = " + studentId + " from " + courseId + " course", e);
        }
    }
}
