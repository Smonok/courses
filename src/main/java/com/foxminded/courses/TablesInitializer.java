package com.foxminded.courses;

import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;

import com.foxminded.courses.dao.TablesDao;
import com.foxminded.courses.util.RandomizerUtil;

public class TablesInitializer {
    private static final Logger LOG = getLogger(TablesInitializer.class);
    private final TablesDao tables;
    private DataSource dataSource;

    public TablesInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
        tables = new TablesDao(dataSource);
    }

    public void initTables() {
        try {
            initGroupsTable();
            initCoursesTable();
            initStudentsTable();
            initStudentsCoursesTable();
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void initGroupsTable() throws SQLException {
        tables.createTable("groups");

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()) {
            for (int i = 0; i < DatabaseConstants.GROUPS_NUMBER; i++) {
                String groupName = generateGroupName();
                String insertGroup = String.format(DatabaseConstants.INSERT_GROUP, i + 1, groupName);

                addGroup(statement, insertGroup);
            }
        } catch (SQLException e) {
            throw new SQLException("Cannot initialize groups table\n", e);
        }
    }

    private String generateGroupName() {
        String data = "ABCDEFGHIJK";
        int firstCharIndex = RandomizerUtil.getRandomNumberBetween(0, data.length() - 1);
        int secondCharIndex = RandomizerUtil.getRandomNumberBetween(0, data.length() - 1);
        int groupNumber = RandomizerUtil.getRandomNumberBetween(10, 99);

        return String.format("%c%c-%d", data.charAt(firstCharIndex), data.charAt(secondCharIndex), groupNumber);
    }

    private void addGroup(Statement statement, String insertQuery) throws SQLException {
        try {
            statement.executeUpdate(insertQuery);
        } catch (SQLException e) {
            throw new SQLException("Cannot add group to the DB\n" + insertQuery, e);
        }
    }

    public void initCoursesTable() throws SQLException {
        tables.createTable("courses");

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()) {
            for (int i = 0; i < DatabaseConstants.COURSES_NUMBER; i++) {
                String insertCourse = String.format(DatabaseConstants.INSERT_COURSE,
                    i + 1,
                    DatabaseConstants.COURSE_NAMES[i], "");

                addCourse(statement, insertCourse);
            }
        } catch (SQLException e) {
            throw new SQLException("Cannot initialize courses table\n", e);
        }
    }

    private void addCourse(Statement statement, String insertQuery) throws SQLException {
        try {
            statement.executeUpdate(insertQuery);
        } catch (SQLException e) {
            throw new SQLException("Cannot add course to the DB\n" + insertQuery, e);
        }
    }

    public void initStudentsTable() throws SQLException {
        tables.createTable("students");

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()) {
            for (int i = 0; i < DatabaseConstants.STUDENTS_NUMBER; i++) {
                int groupId = RandomizerUtil.getRandomNumberBetween(1, 10);
                int firstNameIndex = RandomizerUtil.getRandomNumberBetween(0, DatabaseConstants.FIRST_NAMES.length - 1);
                int lastNameIndex = RandomizerUtil.getRandomNumberBetween(0, DatabaseConstants.LAST_NAMES.length - 1);

                String insertStudent = String.format(DatabaseConstants.INSERT_STUDENT,
                    i + 1,
                    DatabaseConstants.FIRST_NAMES[firstNameIndex],
                    DatabaseConstants.LAST_NAMES[lastNameIndex],
                    groupId);

                addStudent(statement, insertStudent);
            }
        } catch (SQLException e) {
            throw new SQLException("Cannot initialize students table\n", e);
        }
    }

    private void addStudent(Statement statement, String insertQuery) throws SQLException {
        try {
            statement.executeUpdate(insertQuery);
        } catch (SQLException e) {
            throw new SQLException("Cannot add student to the DB\n" + insertQuery, e);
        }
    }

    public void initStudentsCoursesTable() throws SQLException {
        tables.createTable("students_courses");

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()) {
            insertCoursesForEachStudent(statement);
        } catch (SQLException e) {
            throw new SQLException("Cannot initialize students courses table\n", e);
        }
    }

    private void insertCoursesForEachStudent(Statement statement) {
        List<Integer> courses = new ArrayList<>();

        for (int i = 1; i <= DatabaseConstants.COURSES_NUMBER; i++) {
            courses.add(i);
        }

        for (int i = 0; i < DatabaseConstants.STUDENTS_NUMBER; i++) {
            insertCoursesForOneStudent(statement, i + 1, courses);
        }
    }

    private void insertCoursesForOneStudent(Statement statement, int studentId, List<Integer> courses) {
        int coursesPerStudent = RandomizerUtil.getRandomNumberBetween(1, 3);
        Collections.shuffle(courses);

        courses.stream()
            .filter(course -> courses.indexOf(course) < coursesPerStudent)
                .forEach(courseId -> {
                    String insertCourseForStudent = String.format(DatabaseConstants.INSERT_STUDENT_COURSE,
                                studentId, courseId);

                    addCourseForStudent(statement, insertCourseForStudent);
                });
    }

    private void addCourseForStudent(Statement statement, String insertQuery) {
        try {
            statement.executeUpdate(insertQuery);
        } catch (SQLException e) {
            LOG.error("Cannot add course for student\n{}", insertQuery, e);
        }
    }
}
