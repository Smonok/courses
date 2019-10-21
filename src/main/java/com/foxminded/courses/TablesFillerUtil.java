package com.foxminded.courses;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TablesFillerUtil {
  private static final String TABLES_CREATOR_FILE = "create_tables_query.sql";

  public static void fillAllTables(Statement statement) throws SQLException,
    IOException {
    fillGroupsTable(statement);
    fillCoursesTable(statement);
    fillStudentsTable(statement);
    fillStudentsCoursesTable(statement);
  }

  private static void fillGroupsTable(Statement statement) throws SQLException,
    IOException {
    createTable(statement, "groups");

    for (int i = 0; i < DatabaseConstants.GROUPS_NUMBER; i++) {
      String groupName = generateGroupName();
      String insertQuery = String.format(
          "INSERT INTO groups (group_id, group_name) VALUES (%d, '%s');", i + 1,
          groupName);
      statement.executeUpdate(insertQuery);
    }
  }

  private static String generateGroupName() {
    String data = "ABCDEFGHIJK";
    int firstCharIndex = getRandomNumber(0, data.length() - 1);
    int secondCharIndex = getRandomNumber(0, data.length() - 1);
    int groupNumber = getRandomNumber(10, 99);

    return String.format("%c%c-%d", data.charAt(firstCharIndex), data.charAt(
        secondCharIndex), groupNumber);
  }

  private static void fillCoursesTable(Statement statement) throws SQLException,
    IOException {
    createTable(statement, "courses");

    for (int i = 0; i < DatabaseConstants.COURSES_NUMBER; i++) {
      String insertQuery = String.format(
          "INSERT INTO courses (course_id, course_name, course_description) VALUES (%d, '%s', '%s');",
          i + 1, DatabaseConstants.COURSE_NAMES[i], "");
      statement.executeUpdate(insertQuery);
    }
  }

  private static void fillStudentsTable(Statement statement)
    throws SQLException, IOException {
    createTable(statement, "students");

    for (int i = 0; i < DatabaseConstants.STUDENTS_NUMBER; i++) {
      int groupId = getRandomNumber(1, 10);
      int firstNameIndex = getRandomNumber(0, DatabaseConstants.FIRST_NAMES.length - 1);
      int lastNameIndex = getRandomNumber(0, DatabaseConstants.LAST_NAMES.length - 1);

      String insertQuery = String.format(
          "INSERT INTO students (student_id, group_id, first_name, last_name) VALUES (%d, %d, '%s', '%s');",
          i + 1, groupId, DatabaseConstants.FIRST_NAMES[firstNameIndex],
          DatabaseConstants.LAST_NAMES[lastNameIndex]);

      statement.executeUpdate(insertQuery);
    }
  }

  private static void fillStudentsCoursesTable(Statement statement)
    throws SQLException, IOException {
    createTable(statement, "students_courses");

    insertCoursesForEachStudent(statement);
  }

  private static void insertCoursesForEachStudent(Statement statement)
    throws SQLException {
    List<Integer> courses = new ArrayList<>();

    for (int i = 1; i <= DatabaseConstants.COURSES_NUMBER; i++) {
      courses.add(i);
    }

    for (int i = 0; i < DatabaseConstants.STUDENTS_NUMBER; i++) {
      insertCoursesForOneStudent(statement, i + 1, courses);
    }
  }

  private static void insertCoursesForOneStudent(Statement statement,
    int studentId, List<Integer> courses) {
    int coursesPerStudent = getRandomNumber(1, 3);
    Collections.shuffle(courses);

    courses.stream().filter(course -> courses.indexOf(
        course) < coursesPerStudent).forEach(courseId -> {
          String insertQuery = String.format(
              "INSERT INTO students_courses (student_id, course_id) VALUES (%d, %d);",
              studentId, courseId);

          try {
            statement.executeUpdate(insertQuery);
          } catch (SQLException e) {
            e.printStackTrace();
          }
        });
  }

  private static int getRandomNumber(int min, int max) {
    if (min >= max) {
      throw new IllegalArgumentException("max must be greater than min");
    }

    return new Random().ints(min, max + 1).findFirst().orElse(min);
  }

  private static void createTable(Statement statement, String tableName)
    throws IOException {
    QueriesReader reader = new QueriesReader();
    String createQuery = reader.readCreateTableQuery(TABLES_CREATOR_FILE,
        tableName);

    try {
      dropTable(statement, tableName);
      statement.executeUpdate(createQuery);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static void dropTable(Statement statement, String tableName) {
    String dropQuery = "DROP TABLE IF EXISTS " + tableName + " CASCADE;";

    try {
      statement.executeUpdate(dropQuery);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}