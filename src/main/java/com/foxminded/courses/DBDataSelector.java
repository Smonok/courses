package com.foxminded.courses;

import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringJoiner;

import javax.sql.DataSource;

import org.slf4j.Logger;

public class DBDataSelector {
    private static final Logger LOG = getLogger(DBDataSelector.class);
    private final DataSource dataSource;
    private ResultSet resultSet;

    public DBDataSelector(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String selectGroupsWithStudentsNumber(int studentsNumber, int lessOrEquals) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            String groupsWithStudentsCount = createGroupsWithStudentsCountQuery(studentsNumber, lessOrEquals);
            resultSet = statement.executeQuery(groupsWithStudentsCount);

            return parseGroupsWithStudentsNumber();
        } catch (SQLException e) {
            throw new SQLException("Cannot find all groups with " + studentsNumber + " students.", e);
        }
    }

    private String createGroupsWithStudentsCountQuery(int studentsNumber, int input) {
        String groupsWithStudentsCount = "";

        if (input == 1) {
            groupsWithStudentsCount = String.format(DatabaseConstants.GROUPS_WITH_STUDENTS_COUNT, '<', studentsNumber);
        } else if (input == 2) {
            groupsWithStudentsCount = String.format(DatabaseConstants.GROUPS_WITH_STUDENTS_COUNT, '=', studentsNumber);
        }

        return groupsWithStudentsCount;
    }

    private String parseGroupsWithStudentsNumber() throws SQLException {
        StringJoiner groupWithStudents = new StringJoiner("\n");

        while (resultSet.next()) {
            String groupName = resultSet.getString("group_name");
            int count = resultSet.getInt("count");

            groupWithStudents.add(String.format("%s <==> %d", groupName, count));
        }

        if(groupWithStudents.toString().isEmpty()) {
            return "No groups found";
        }

        return groupWithStudents.toString();
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
            int id = resultSet.getInt("student_id");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");

            students.add(String.format("%d. %s %s", id, firstName, lastName));
        }

        return students.toString();
    }

    public String selectAllGroups() throws SQLException {
        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery(DatabaseConstants.ALL_GROUPS);

            return parseGroupsInfo();
        } catch (SQLException e) {
            throw new SQLException("Cannot select all groups.", e);
        }
    }

    private String parseGroupsInfo() throws SQLException {
        StringJoiner groups = new StringJoiner("\n");

        while (resultSet.next()) {
            String groupName = resultSet.getString("group_name");
            int id = resultSet.getInt("group_id");

            groups.add(String.format("%d. %s", id, groupName));
        }

        return groups.toString();
    }

    public String selectAllCourses() throws SQLException {
        LOG.info("List of courses:");

        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery(DatabaseConstants.ALL_COURSES);

            return parseCoursesInfo();
        } catch (SQLException e) {
            throw new SQLException("Cannot select all courses.", e);
        }
    }

    public String selectCoursesByStudentId(int studentId) throws SQLException {
        LOG.info("Courses of student with ID = {}:", studentId);

        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            String studentCourses = String.format(DatabaseConstants.STUDENT_COURSES, studentId);
            resultSet = statement.executeQuery(studentCourses);

            return parseCoursesInfo();
        } catch (SQLException e) {
            throw new SQLException("Cannot select courses by student Id.", e);
        }
    }

    private String parseCoursesInfo() throws SQLException {
        StringJoiner courses = new StringJoiner("\n");

        while (resultSet.next()) {
            int id = resultSet.getInt("course_id");
            String name = resultSet.getString("course_name");

            courses.add(String.format("%d. %s", id, name));
        }

        return courses.toString();
    }
}
