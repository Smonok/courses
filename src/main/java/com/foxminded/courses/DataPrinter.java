package com.foxminded.courses;

import static org.slf4j.LoggerFactory.getLogger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;

public class DataPrinter {
    private static final Logger LOG = getLogger(DataPrinter.class);
    private String exceptionMessage;
    private ResultSet resultSet;

    ResultSet printGroupsAndStudentsNumber(Statement statement, int studentsNumber, int lessOrEquals)
        throws SQLException {
        String groupsAndStudentsNumber;
        try {
            groupsAndStudentsNumber = selectGroupsWithStudentsCount(studentsNumber, lessOrEquals);

            if (!groupsAndStudentsNumber.equals("")) {
                resultSet = statement.executeQuery(groupsAndStudentsNumber);

                while (resultSet.next()) {
                    String groupName = resultSet.getString("group_name");
                    int count = resultSet.getInt("count");

                    LOG.info("{} <==> {}", groupName, count);
                }
            }
        } catch (SQLException e) {
            exceptionMessage = String.format("Cannot find all groups with %d students.", studentsNumber);
            throw new SQLException(exceptionMessage);
        }

        return resultSet;
    }

    private String selectGroupsWithStudentsCount(int studentsNumber, int input) {
        if (input == 1) {
            return String.format(DatabaseConstants.GROUPS_WITH_STUDENTS_COUNT, '<', studentsNumber);
        } else if (input == 2) {
            return String.format(DatabaseConstants.GROUPS_WITH_STUDENTS_COUNT, '=', studentsNumber);
        }

        return "";
    }

    ResultSet printStudentsRelatedToCourse(Statement statement, int courseId) throws SQLException {
        try {
            String courseName = getCourseNameById(statement, courseId);
            String selectedStudents = String.format(DatabaseConstants.STUDENTS_RELATED_TO_COURSE, courseId);
            resultSet = statement.executeQuery(selectedStudents);

            LOG.info("Students related to {} course:", courseName);
            printParsedStudentsInfo(resultSet);
        } catch (SQLException e) {
            exceptionMessage = String.format("Cannot find student related to %d course.", courseId);
            throw new SQLException(exceptionMessage);
        }

        return resultSet;
    }

    private String getCourseNameById(Statement statement, int courseId) throws SQLException {
        resultSet = statement.executeQuery(String.format(DatabaseConstants.COURSE_NAME_BY_ID, courseId));
        resultSet.next();
        return resultSet.getString("course_name");
    }

    ResultSet printAllStudents(Statement statement) throws SQLException {
        try {
            resultSet = statement.executeQuery(DatabaseConstants.ALL_STUDENTS);
            printParsedStudentsInfo(resultSet);
        } catch (SQLException e) {
            throw new SQLException("Cannot print all students.");
        }

        return resultSet;
    }

    private void printParsedStudentsInfo(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            int id = resultSet.getInt("student_id");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");

            LOG.info("{}. {} {}", id, firstName, lastName);
        }
    }

    ResultSet printAllGroups(Statement statement) throws SQLException {
        try {
            resultSet = statement.executeQuery(DatabaseConstants.ALL_GROUPS);
            printParsedGroupsInfo(resultSet);
        } catch (SQLException e) {
            throw new SQLException("Cannot print all groups.");
        }

        return resultSet;
    }

    private void printParsedGroupsInfo(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            String groupName = resultSet.getString("group_name");
            int id = resultSet.getInt("group_id");

            LOG.info("{}. {}", id, groupName);
        }
    }

    ResultSet printAllCourses(Statement statement) throws SQLException {
        LOG.info("List of courses:");

        try {
            resultSet = statement.executeQuery(DatabaseConstants.ALL_COURSES);
            printParsedCoursesInfo(resultSet);
        } catch (SQLException e) {
            throw new SQLException("Cannot print all courses.");
        }

        return resultSet;
    }

    ResultSet printCoursesByStudentId(Statement statement, int studentId) throws SQLException {
        LOG.info("His (her) courses:");

        String studentCourses = String.format(DatabaseConstants.STUDENT_COURSES, studentId);
        resultSet = statement.executeQuery(studentCourses);

        printParsedCoursesInfo(resultSet);

        return resultSet;
    }

    private void printParsedCoursesInfo(ResultSet resultSet) throws SQLException  {
        while (resultSet.next()) {
            int id = resultSet.getInt("course_id");
            String name = resultSet.getString("course_name");

            LOG.info("{}. {}", id, name);
        }
    }
}
