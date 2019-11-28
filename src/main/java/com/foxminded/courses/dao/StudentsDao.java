package com.foxminded.courses.dao;

import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringJoiner;

import javax.sql.DataSource;

import org.slf4j.Logger;

import com.foxminded.courses.DatabaseConstants;

public class StudentsDao {
    private static final String STUDENT_ID = "student_id";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String EXISTS = "is_exists";
    private static final Logger LOG = getLogger(StudentsDao.class);
    private final DataSource dataSource;
    private ResultSet resultSet;

    public StudentsDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isStudentExists(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery(String.format(DatabaseConstants.IS_STUDENT_EXISTS, id));

            resultSet.next();
            return resultSet.getBoolean(EXISTS);
        } catch (SQLException e) {
            throw new SQLException("Cannot check if student with id = " + id + " exists", e);
        }
    }

    public void addStudent(String firstName, String lastName, int groupId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            int studentId = DatabaseConstants.STUDENTS_NUMBER;

            statement.executeUpdate(String.format(DatabaseConstants.INSERT_STUDENT, ++studentId, firstName, lastName,
                        groupId));
            LOG.info("Student with id = {}: {} {} added successfully", studentId, firstName, lastName);
        } catch (SQLException e) {
            throw new SQLException("Cannot add student: " + firstName + " " + lastName + ", group ID = " + groupId, e);
        }
    }

    public void deleteStudentById(int studentId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            String deleteStudent = String.format(DatabaseConstants.DELETE_STUDENT_BY_ID, studentId);

            statement.executeUpdate(deleteStudent);
            LOG.info("Student with id = {} successfully deleted", studentId);
        } catch (SQLException e) {
            throw new SQLException("Cannot delete student with ID = " + studentId, e);
        }
    }

    public String selectStudentsByCourse(int courseId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            String courseName = getCourseNameById(statement, courseId);
            String selectedStudents = String.format(DatabaseConstants.STUDENTS_BY_COURSE, courseId);
            resultSet = statement.executeQuery(selectedStudents);

            LOG.info("Students related to {} course:", courseName);
            return parseStudentsInfo();
        } catch (SQLException e) {
            throw new SQLException("Cannot find student related to " + courseId + " course.", e);
        }
    }

    private String getCourseNameById(Statement statement, int courseId) throws SQLException {
        resultSet = statement.executeQuery(String.format(DatabaseConstants.COURSE_NAME_BY_ID, courseId));
        resultSet.next();
        return resultSet.getString("course_name");
    }

    public String selectAllStudents() throws SQLException {
        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery(DatabaseConstants.ALL_STUDENTS);

            return parseStudentsInfo();
        } catch (SQLException e) {
            throw new SQLException("Cannot select all students.", e);
        }
    }

    private String parseStudentsInfo() throws SQLException {
        StringJoiner students = new StringJoiner("\n");

        while (resultSet.next()) {
            int id = resultSet.getInt(STUDENT_ID);
            String firstName = resultSet.getString(FIRST_NAME);
            String lastName = resultSet.getString(LAST_NAME);

            students.add(String.format("%d. %s %s", id, firstName, lastName));
        }

        return students.toString();
    }
}
