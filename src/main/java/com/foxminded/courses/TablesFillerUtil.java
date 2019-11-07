package com.foxminded.courses;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public final class TablesFillerUtil {
    private static final String TABLES_CREATOR_FILE = "create_tables_query.sql";
    private static HashSet<Integer> studentsId = new HashSet<>();
    private static HashSet<Integer> coursesId = new HashSet<>();
    private static HashSet<Integer> groupsId = new HashSet<>();

    static void fillAllTables(Statement statement) {
        try {
            fillGroupsTable(statement);
            fillCoursesTable(statement);
            fillStudentsTable(statement);
            fillStudentsCoursesTable(statement);
        } catch (SQLException e) {
            InitializerUtil.log.error(e.getMessage());
        }
    }

    static boolean doesStudentExists(int id) {
        return studentsId.contains(id);
    }

    static boolean doesGroupExists(int id) {
        return groupsId.contains(id);
    }

    static boolean doesCourseExists(int id) {
        return coursesId.contains(id);
    }

    static void fillGroupsTable(Statement statement) throws SQLException {
        createTable(statement, "groups");

        for (int i = 0; i < DatabaseConstants.GROUPS_NUMBER; i++) {
            String groupName = generateGroupName();
            String insertGroup = String.format(DatabaseConstants.INSERT_GROUP, i + 1, groupName);
            try {
                statement.executeUpdate(insertGroup);
                groupsId.add(i + 1);
            } catch (SQLException e) {
                InitializerUtil.log.error("Cannot fill group table", e);
                throw new SQLException("Cannot fill group table", e);
            }
        }
    }

    private static String generateGroupName() {
        String data = "ABCDEFGHIJK";
        int firstCharIndex = getRandomNumber(0, data.length() - 1);
        int secondCharIndex = getRandomNumber(0, data.length() - 1);
        int groupNumber = getRandomNumber(10, 99);

        return String.format("%c%c-%d", data.charAt(firstCharIndex), data.charAt(secondCharIndex), groupNumber);
    }

    static void fillCoursesTable(Statement statement) throws SQLException {
        createTable(statement, "courses");

        for (int i = 0; i < DatabaseConstants.COURSES_NUMBER; i++) {
            String insertCourse = String.format(DatabaseConstants.INSERT_COURSE, i + 1, DatabaseConstants.COURSE_NAMES[i],
                            "");
            try {
                statement.executeUpdate(insertCourse);
                coursesId.add(i + 1);
            } catch (SQLException e) {
                InitializerUtil.log.error("Cannot fill courses table", e);
                throw new SQLException("Cannot fill courses table", e);
            }
        }
    }

    static void fillStudentsTable(Statement statement) throws SQLException {
        createTable(statement, "students");

        for (int i = 0; i < DatabaseConstants.STUDENTS_NUMBER; i++) {
            int groupId = getRandomNumber(1, 10);
            int firstNameIndex = getRandomNumber(0, DatabaseConstants.FIRST_NAMES.length - 1);
            int lastNameIndex = getRandomNumber(0, DatabaseConstants.LAST_NAMES.length - 1);

            String insertStudent = String.format(DatabaseConstants.INSERT_STUDENT, i + 1,
                            DatabaseConstants.FIRST_NAMES[firstNameIndex], DatabaseConstants.LAST_NAMES[lastNameIndex],
                            groupId);

            try {
                statement.executeUpdate(insertStudent);
                studentsId.add(i + 1);
            } catch (SQLException e) {
                InitializerUtil.log.error("Cannot fill students table", e);
                throw new SQLException("Cannot fill students table", e);
            }
        }
    }

    static void fillStudentsCoursesTable(Statement statement) throws SQLException {
        createTable(statement, "students_courses");

        insertCoursesForEachStudent(statement);
    }

    private static void insertCoursesForEachStudent(Statement statement) throws SQLException {
        List<Integer> courses = new ArrayList<>();

        for (int i = 1; i <= DatabaseConstants.COURSES_NUMBER; i++) {
            courses.add(i);
        }

        for (int i = 0; i < DatabaseConstants.STUDENTS_NUMBER; i++) {
            insertCoursesForOneStudent(statement, i + 1, courses);
        }
    }

    private static void insertCoursesForOneStudent(Statement statement, int studentId, List<Integer> courses) {
        int coursesPerStudent = getRandomNumber(1, 3);
        Collections.shuffle(courses);

        courses.stream().filter(course -> courses.indexOf(course) < coursesPerStudent).forEach(courseId -> {
            String addCourseForStudent = String.format(DatabaseConstants.INSERT_STUDENT_COURSE, studentId, courseId);

            try {
                statement.executeUpdate(addCourseForStudent);
            } catch (SQLException e) {
                InitializerUtil.log.error("Cannot add courses for student", e);
            }

        });
    }

    private static int getRandomNumber(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        return new Random().ints(min, max + 1).findFirst().orElse(min);
    }

    private static void createTable(Statement statement, String tableName) throws SQLException {
        QueriesReader reader = new QueriesReader();
        String createTable = reader.readTableСreationQuery(TABLES_CREATOR_FILE, tableName);

        dropTable(statement, tableName);
        try {
            statement.executeUpdate(createTable);
        } catch (SQLException e) {
            InitializerUtil.log.error("Cannot create %s table", tableName, e);
            throw new SQLException("Cannot create table", e);
        }
    }

    private static void dropTable(Statement statement, String tableName) throws SQLException {
        String dropTable = String.format(DatabaseConstants.DROP_TABLE, tableName);

        try {
            statement.executeUpdate(dropTable);
        } catch (SQLException e) {
            InitializerUtil.log.error("Cannot drop %s table", tableName, e);
            throw new SQLException("Cannot drop table", e);
        }
    }

    private TablesFillerUtil() {
        throw new UnsupportedOperationException();
    }
}
