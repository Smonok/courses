package com.foxminded.courses;

import static org.slf4j.LoggerFactory.getLogger;

import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;

public class DataController {
    private static final Logger LOG = getLogger(DataController.class);
    private String exceptionMessage;

    void addStudent(Statement statement, String firstName, String lastName, int groupId) throws SQLException {
        try {
            int studentId = DatabaseConstants.STUDENTS_NUMBER;

            statement.executeUpdate(String.format(DatabaseConstants.INSERT_STUDENT, ++studentId, firstName, lastName,
                            groupId));
            LOG.info("Student added successfully");
        } catch (SQLException e) {
            exceptionMessage = String.format("Cannot add new student: %s %s, group ID = %d", firstName, lastName,
                            groupId);
            throw new SQLException(exceptionMessage);
        }
    }

    void deleteStudentById(Statement statement, int studentId) throws SQLException {
        try {
            String deleteStudent = String.format(DatabaseConstants.DELETE_STUDENT_BY_ID, studentId);
            statement.executeUpdate(deleteStudent);
            LOG.info("Student successfully deleted");
        } catch (SQLException e) {
            exceptionMessage = String.format("Cannot delete student with ID = %d", studentId);
            throw new SQLException(exceptionMessage);
        }
    }

    void addCourseToStudent(Statement statement, int studentId, int courseId) throws SQLException {
        try {
            String addCourseForStudent = String.format(DatabaseConstants.INSERT_STUDENT_COURSE, studentId, courseId);

            statement.executeUpdate(addCourseForStudent);
            LOG.info("Course added successfully");
        } catch (SQLException e) {
            exceptionMessage = String.format("Cannot add student with ID = %d to %d course", studentId, courseId);
            throw new SQLException(exceptionMessage);
        }
    }

    void removeStudentFromCourse(Statement statement, int studentId, int courseId) throws SQLException {
        try {
            String deleteStudentCourse = String.format(DatabaseConstants.DELETE_STUDENT_FROM_COURSE, studentId,
                            courseId);
            statement.executeUpdate(deleteStudentCourse);
            LOG.info("Course deleted successfully");
        } catch (SQLException e) {
            exceptionMessage = String.format("Cannot remove student with ID = %d from %d course", studentId, courseId);
            throw new SQLException(exceptionMessage);
        }
    }
}
