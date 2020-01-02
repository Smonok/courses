package com.foxminded.courses.db.dao;

import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringJoiner;

import javax.sql.DataSource;

import org.slf4j.Logger;

import com.foxminded.courses.constants.DatabaseConstants;

public class StudentsDao {
    private static final String STUDENT_ID = "student_id";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String EXISTS = "is_exists";
    private static final Logger LOG = getLogger(StudentsDao.class);
    private final DataSource dataSource;

    public StudentsDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isStudentExists(int id) throws SQLException {
        String isExists = String.format(DatabaseConstants.IS_STUDENT_EXISTS, id);

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(isExists)) {

            resultSet.next();
            return resultSet.getBoolean(EXISTS);
        } catch (SQLException e) {
            throw new SQLException("Cannot check if student with id = " + id + " exists: " + isExists, e);
        }
    }

    public void addStudent(String firstName, String lastName, int groupId) throws SQLException {
        int studentId = DatabaseConstants.STUDENTS_NUMBER;
        String insert = String.format(DatabaseConstants.INSERT_STUDENT, ++studentId, firstName, lastName, groupId);

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()) {

            statement.executeUpdate(insert);
            LOG.info("Student with id = {}: {} {} added successfully", studentId, firstName, lastName);
        } catch (SQLException e) {
            throw new SQLException("Cannot add student: " + firstName + " " + lastName + ", group ID = " + groupId
                + ": " + insert, e);
        }
    }

    public void deleteStudentById(int studentId) throws SQLException {
        String deleteStudent = String.format(DatabaseConstants.DELETE_STUDENT_BY_ID, studentId);

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()) {

            statement.executeUpdate(deleteStudent);
            LOG.info("Student with id = {} successfully deleted", studentId);
        } catch (SQLException e) {
            throw new SQLException("Cannot delete student with ID = " + studentId + ": " + deleteStudent, e);
        }
    }

    public String selectStudentsByCourse(int courseId) throws SQLException {
        String selectStudents = String.format(DatabaseConstants.STUDENTS_BY_COURSE, courseId);

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(selectStudents)) {
            CoursesDao courses = new CoursesDao(dataSource);
            String courseName = courses.getCourseNameById(courseId);

            LOG.info("Students related to {} course:", courseName);
            return combineStudentsInfo(resultSet);
        } catch (SQLException e) {
            throw new SQLException("Cannot find student related to " + courseId + " course.: " + selectStudents, e);
        }
    }

    public String selectAllStudents() throws SQLException {
        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(DatabaseConstants.ALL_STUDENTS)) {

            return combineStudentsInfo(resultSet);
        } catch (SQLException e) {
            throw new SQLException("Cannot select all students.: " + DatabaseConstants.ALL_STUDENTS, e);
        }
    }

    private String combineStudentsInfo(ResultSet resultSet) throws SQLException {
        StringJoiner students = new StringJoiner("\n");

        while (resultSet.next()) {
            students.add(parseStudentInfo(resultSet));
        }

        return students.toString();
    }

    private String parseStudentInfo(ResultSet resultSet) throws SQLException {

        int id = resultSet.getInt(STUDENT_ID);
        String firstName = resultSet.getString(FIRST_NAME);
        String lastName = resultSet.getString(LAST_NAME);

        return String.format("%d. %s %s", id, firstName, lastName);
    }
}
