package com.foxminded.courses;

import static org.slf4j.LoggerFactory.getLogger;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

public final class TablesUtil {
    private static final Logger LOG = getLogger(TablesUtil.class);
    private static final TablesController tablesController = new TablesController();
    private static Set<Integer> studentsId = new HashSet<>();
    private static Set<Integer> coursesId = new HashSet<>();
    private static Set<Integer> groupsId = new HashSet<>();

    static void fillAllTables(Statement statement) {
        try {
            fillGroupsTable(statement);
            fillCoursesTable(statement);
            fillStudentsTable(statement);
            fillStudentsCoursesTable(statement);
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
    }

    static boolean isStudentExists(int id) {
        return studentsId.contains(id);
    }

    static boolean isGroupExists(int id) {
        return groupsId.contains(id);
    }

    static boolean isCourseExists(int id) {
        return coursesId.contains(id);
    }

    static void fillGroupsTable(Statement statement) throws SQLException {
        tablesController.createTable(statement, "groups");

        for (int i = 0; i < DatabaseConstants.GROUPS_NUMBER; i++) {
            String groupName = generateGroupName();
            String insertGroup = String.format(DatabaseConstants.INSERT_GROUP, i + 1, groupName);

            addGroup(statement, insertGroup, i + 1);
        }
    }

    private static String generateGroupName() {
        String data = "ABCDEFGHIJK";
        int firstCharIndex = RandomizerUtil.getRandomNumberBetween(0, data.length() - 1);
        int secondCharIndex = RandomizerUtil.getRandomNumberBetween(0, data.length() - 1);
        int groupNumber = RandomizerUtil.getRandomNumberBetween(10, 99);

        return String.format("%c%c-%d", data.charAt(firstCharIndex), data.charAt(secondCharIndex), groupNumber);
    }

    private static void addGroup(Statement statement, String insertQuery, int id) throws SQLException {
        try {
            statement.executeUpdate(insertQuery);
            groupsId.add(id);
        } catch (SQLException e) {
            throw new SQLException("Cannot add group to the DB");
        }
    }

    static void fillCoursesTable(Statement statement) throws SQLException {
        tablesController.createTable(statement, "courses");

        for (int i = 0; i < DatabaseConstants.COURSES_NUMBER; i++) {
            String insertCourse = String.format(DatabaseConstants.INSERT_COURSE, i + 1,
                            DatabaseConstants.COURSE_NAMES[i], "");

            addCourse(statement, insertCourse, i + 1);
        }
    }

    private static void addCourse(Statement statement, String insertQuery, int id) throws SQLException {
        try {
            statement.executeUpdate(insertQuery);
            coursesId.add(id);
        } catch (SQLException e) {
            throw new SQLException("Cannot add course to the DB");
        }
    }

    static void fillStudentsTable(Statement statement) throws SQLException {
        tablesController.createTable(statement, "students");

        for (int i = 0; i < DatabaseConstants.STUDENTS_NUMBER; i++) {
            int groupId = RandomizerUtil.getRandomNumberBetween(1, 10);
            int firstNameIndex = RandomizerUtil.getRandomNumberBetween(0, DatabaseConstants.FIRST_NAMES.length - 1);
            int lastNameIndex = RandomizerUtil.getRandomNumberBetween(0, DatabaseConstants.LAST_NAMES.length - 1);

            String insertStudent = String.format(DatabaseConstants.INSERT_STUDENT, i + 1,
                            DatabaseConstants.FIRST_NAMES[firstNameIndex], DatabaseConstants.LAST_NAMES[lastNameIndex],
                            groupId);

            addStudent(statement, insertStudent, i + 1);
        }
    }

    private static void addStudent(Statement statement, String insertQuery, int id) throws SQLException {
        try {
            statement.executeUpdate(insertQuery);
            studentsId.add(id);
        } catch (SQLException e) {
            throw new SQLException("Cannot add student to the DB");
        }
    }

    static void fillStudentsCoursesTable(Statement statement) throws SQLException {
        tablesController.createTable(statement, "students_courses");

        insertCoursesForEachStudent(statement);
    }

    private static void insertCoursesForEachStudent(Statement statement) {
        List<Integer> courses = new ArrayList<>();

        for (int i = 1; i <= DatabaseConstants.COURSES_NUMBER; i++) {
            courses.add(i);
        }

        for (int i = 0; i < DatabaseConstants.STUDENTS_NUMBER; i++) {
            insertCoursesForOneStudent(statement, i + 1, courses);
        }
    }

    private static void insertCoursesForOneStudent(Statement statement, int studentId, List<Integer> courses) {
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

    private static void addCourseForStudent(Statement statement, String insertQuery) {
        try {
            statement.executeUpdate(insertQuery);
        } catch (SQLException e) {
            LOG.error("Cannot add course for student");
        }
    }

    private TablesUtil() {
        throw new UnsupportedOperationException();
    }
}
