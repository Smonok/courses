package com.foxminded.courses;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StudentsController {
    private ResultSet resultSet;
    private final Logger LOG = LoggerFactory.getLogger(StudentsController.class);

    ResultSet printGroupsAndStudentsNumber(Statement statement, int studentsNumber, int lessOrEquals)
        throws SQLException {
        String groupsAndStudentsNumber;
        try {
            groupsAndStudentsNumber = createQueryDependingOnEnteredData(studentsNumber, lessOrEquals);

            if (groupsAndStudentsNumber != null) {
                resultSet = statement.executeQuery(groupsAndStudentsNumber);

                while (resultSet.next()) {
                    String groupName = resultSet.getString("group_name");
                    int count = resultSet.getInt("count");

                    System.out.println(String.format("%-6s   %6d", groupName, count));
                }
            }
        } catch (SQLException e) {
            LOG.error("Cannot find all groups with " + studentsNumber + " students.", e);
            throw e;
        }

        return resultSet;
    }

    private String createQueryDependingOnEnteredData(int studentsNumber, int input) {
        if (input == 1) {
            return String.format(DatabaseConstants.GROUPS_WITH_STUDENTS_COUNT, '<', studentsNumber);
        } else if (input == 2) {
            return String.format(DatabaseConstants.GROUPS_WITH_STUDENTS_COUNT, '=', studentsNumber);
        }

        return null;
    }

    ResultSet printStudentsRelatedToCourse(Statement statement, int courseId) throws SQLException {
        try {
            String courseName = getCourseNameById(statement, courseId);
            String selectedStudents = String.format(DatabaseConstants.STUDENTS_RELATED_TO_COURSE, courseId);
            resultSet = statement.executeQuery(selectedStudents);

            System.out.println("Students related to " + courseName + " course:");
            while (resultSet.next()) {
                int id = resultSet.getInt("student_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                System.out.println(id + ". " + firstName + " " + lastName);
            }
        } catch (SQLException e) {
            LOG.error("Cannot find student related to " + courseId + " course.", e);
            throw e;
        }

        return resultSet;
    }

    private String getCourseNameById(Statement statement, int courseId) throws SQLException {
        resultSet = statement.executeQuery(String.format(DatabaseConstants.COURSE_NAME_BY_ID, courseId));
        resultSet.next();
        return resultSet.getString("course_name");
    }

    void addStudent(Statement statement, String firstName, String lastName, int groupId) throws SQLException {
        try {
            int studentId = DatabaseConstants.STUDENTS_NUMBER;

            statement.executeUpdate(String.format(DatabaseConstants.INSERT_STUDENT, ++studentId, firstName, lastName,
                            groupId));
            System.out.println("Student added successfully");
        } catch (SQLException e) {
            LOG.error("Cannot add new student: " + firstName + " " + lastName + ", group ID = " + groupId, e);
            throw e;
        }
    }

    void deleteStudentById(Statement statement, int studentId) throws SQLException {
        try {
            String deleteStudent = String.format(DatabaseConstants.DELETE_STUDENT_BY_ID, studentId);
            statement.executeUpdate(deleteStudent);
            System.out.println("Student successfully deleted");
        } catch (SQLException e) {
            LOG.error("Cannot delete student with ID = " + studentId, e);
            throw e;
        }
    }

    void addCourseToStudent(Statement statement, int studentId, int courseId) throws SQLException {
        try {
            String addCourseForStudent = String.format(DatabaseConstants.INSERT_STUDENT_COURSE, studentId, courseId);

            statement.executeUpdate(addCourseForStudent);
            System.out.println("Course added successfully");
        } catch (SQLException e) {
            LOG.error("Cannot add student with ID = " + studentId + " to " + courseId + " course.", e);
            throw e;
        }
    }

    void removeStudentFromCourse(Statement statement, int studentId, int courseId) throws SQLException {
        try {
            String deleteStudentCourse = String.format(DatabaseConstants.DELETE_STUDENT_FROM_COURSE, studentId,
                            courseId);
            statement.executeUpdate(deleteStudentCourse);
            System.out.println("Course deleted successfully");
        } catch (SQLException e) {
            LOG.error("Cannot remove student with ID = " + studentId + " from " + courseId + " course.", e);
            throw e;
        }
    }
}
