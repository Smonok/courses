package com.foxminded.courses;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {
  private final Scanner scanner = new Scanner(System.in);
  private ResultSet resultSet;
  private final String WRONG_INPUT_MESSAGE =
      "Error! The entered number does not match any of the proposed";

  public void displayMenu(Statement statement) {
    System.out.println("MENU");

    System.out.println(
        "1. Enter 1 to find all groups with less or equals student count");
    System.out.println(
        "2. Enter 2 to find all students related to course with given name");
    System.out.println("3. Enter 3 to add new student");
    System.out.println("4. Enter 4 to delete student by student id");
    System.out.println("5. Enter 5 to add a student to the course");
    System.out.println(
        "6. Enter 6 to remove the student from one of his or her courses");

    System.out.print("You choose: ");
    int input = scanner.nextInt();
    try {
      switch (input) {
        case (1):
          printGroupsAndStudentsNumber(statement);
          break;
        case (2):
          printStudentsRelatedToCourse(statement);
          break;
        case (3):
          addStudent(statement);
          break;
        case (4):
          deleteStudentById(statement);
          break;
        case (5):
          addCourseToStudent(statement);
          break;
        case (6):
          removeStudentFromCourse(statement);
          break;
        default:
          System.out.println(WRONG_INPUT_MESSAGE);
          break;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    System.out.println("Work with the database was finished");
  }

  private void printGroupsAndStudentsNumber(Statement statement)
    throws SQLException {
    int number = getNumberToCompareStudents();
    int input = findGroupsWithLessOrEqualsStudentsNumber(number);

    if (input < 1 || input > 2) {
      System.out.println(WRONG_INPUT_MESSAGE);
      return;
    }

    String groupsAndStudentsNumberQuery = findGroupsRegardingToStudentsNumber(
        input, number);
    resultSet = statement.executeQuery(groupsAndStudentsNumberQuery);

    printGroupsAndStudentsNumber();
  }

  private int getNumberToCompareStudents() {
    System.out.print(
        "Enter the number with which you will compare the number of students: ");

    return scanner.nextInt();
  }

  private int findGroupsWithLessOrEqualsStudentsNumber(int number) {
    System.out.println(
        "-> Enter 1 if you want to find groups with students count less then "
            + number);
    System.out.println(
        "-> Enter 2 if you want to find groups with students count that equals "
            + number);
    System.out.print("-> You choose: ");

    return scanner.nextInt();
  }

  private String findGroupsRegardingToStudentsNumber(int input, int studentsNumber) {
    if (input == 1) {
      return String.format(DatabaseConstants.GROUPS_WITH_STUDENTS_COUNT_QUERY,
          '<', studentsNumber);
    } else if (input == 2) {
      return String.format(DatabaseConstants.GROUPS_WITH_STUDENTS_COUNT_QUERY,
          '=', studentsNumber);
    }

    return "";
  }

  private void printGroupsAndStudentsNumber() throws SQLException {
    System.out.println("group_name | students count");
    while (resultSet.next()) {
      String groupName = resultSet.getString("group_name");
      int count = resultSet.getInt("count");

      System.out.println(String.format("%-6s   %6d", groupName, count));
    }
  }

  private void printStudentsRelatedToCourse(Statement statement)
    throws SQLException {
    printAllCourses();
    int courseId = getCourseId();

    if (courseId < 1 || courseId > DatabaseConstants.COURSES_NUMBER) {
      System.out.println(WRONG_INPUT_MESSAGE);
      return;
    }

    String courseName = getCourseNameById(statement, courseId);

    System.out.println("Students related to " + courseName + " course:");
    printStudents(statement, String.format(
        DatabaseConstants.STUDENTS_RELATED_TO_COURSE_QUERY, courseId));
  }

  private String getCourseNameById(Statement statement, int courseId)
    throws SQLException {
    resultSet = statement.executeQuery(
        "SELECT * FROM courses WHERE course_id = " + courseId);
    resultSet.next();

    return resultSet.getString("course_name");
  }

  private void addStudent(Statement statement) throws SQLException {
    String firstName = getName("first");
    scanner.nextLine();
    String lastName = getName("last");

    printAllGroups(statement);

    int groupId = getGroupId();
    if (groupId < 1 || groupId > DatabaseConstants.GROUPS_NUMBER) {
      System.out.println(WRONG_INPUT_MESSAGE);
      return;
    }

    int studentId = DatabaseConstants.STUDENTS_NUMBER;

    statement.executeUpdate(String.format(DatabaseConstants.ADD_STUDENT_QUERY,
        ++studentId, firstName, lastName, groupId));

    System.out.println("Student added successfully");
  }

  private String getName(String nameNumber) {
    System.out.print("Enter student's " + nameNumber + " name: ");
    return scanner.nextLine();
  }

  private void printAllGroups(Statement statement) throws SQLException {
    resultSet = statement.executeQuery("SELECT * FROM groups;");
    while (resultSet.next()) {
      String groupName = resultSet.getString("group_name");
      int id = resultSet.getInt("group_id");

      System.out.println(id + ". " + groupName);
    }
  }

  private int getGroupId() {
    System.out.print("Enter student's group: ");
    return scanner.nextInt();
  }

  private void deleteStudentById(Statement statement) throws SQLException {
    System.out.println("All students:");
    printStudents(statement, DatabaseConstants.SELECT_ALL_STUDENTS_QUERY);

    System.out.print("Enter id of the student which you want to delete: ");
    int studentId = scanner.nextInt();

    if (studentId < 1 || studentId > DatabaseConstants.STUDENTS_NUMBER) {
      System.out.println(WRONG_INPUT_MESSAGE);
      return;
    }

    statement.executeUpdate(String.format(
        "DELETE FROM students WHERE student_id = %d;", studentId));

    System.out.println("Student successfully deleted");
  }

  private void addCourseToStudent(Statement statement) throws SQLException {
    System.out.println("All students:");
    printStudents(statement, DatabaseConstants.SELECT_ALL_STUDENTS_QUERY);

    System.out.print(
        "Enter id of the student for which you want to add a course: ");
    int studentId = scanner.nextInt();

    if (studentId < 1 || studentId > DatabaseConstants.STUDENTS_NUMBER) {
      System.out.println(WRONG_INPUT_MESSAGE);
      return;
    }

    printAllCourses();

    System.out.print("Enter the course which you want to add: ");
    int courseId = getCourseId();

    if (courseId < 1 || courseId > DatabaseConstants.COURSES_NUMBER) {
      System.out.println(WRONG_INPUT_MESSAGE);
      return;
    }

    String insertIntoStudentsCoursesQuery = String.format(
        "INSERT INTO students_courses(student_id, course_id) VALUES(%d, %d)",
        studentId, courseId);

    statement.executeUpdate(insertIntoStudentsCoursesQuery);

    System.out.print("Course added successfully");
  }

  private void printAllCourses() {
    System.out.println("List of courses:");
    int i = 0;
    for (String course : DatabaseConstants.COURSE_NAMES) {
      System.out.println(++i + ". " + course);
    }
  }

  private int getCourseId() {
    System.out.print("Enter the course id: ");

    return scanner.nextInt();
  }

  private void removeStudentFromCourse(Statement statement)
    throws SQLException {
    System.out.println("All students:");
    printStudents(statement, DatabaseConstants.SELECT_ALL_STUDENTS_QUERY);

    System.out.print("Enter id of the student which you choose: ");
    int studentId = scanner.nextInt();

    if (studentId < 1 || studentId > DatabaseConstants.STUDENTS_NUMBER) {
      System.out.println(WRONG_INPUT_MESSAGE);
      return;
    }

    List<Integer> courses = new ArrayList<>();
    printOneStudentCourses(statement, studentId, courses);

    System.out.print("Enter id of a course which you want to delete: ");
    int courseId = scanner.nextInt();

    if (!courses.contains(courseId)) {
      System.out.println(WRONG_INPUT_MESSAGE);
    } else {
      deleteCourse(statement, studentId, courseId);
    }
  }

  private void printStudents(Statement statement, final String query)
    throws SQLException {
    resultSet = statement.executeQuery(query);

    while (resultSet.next()) {
      int id = resultSet.getInt("student_id");
      String firstName = resultSet.getString("first_name");
      String lastName = resultSet.getString("last_name");

      System.out.println(id + ". " + firstName + " " + lastName);
    }
  }

  private void printOneStudentCourses(Statement statement, int studentId,
    List<Integer> courses) throws SQLException {
    System.out.println("His (her) courses:");

    resultSet = statement.executeQuery(String.format(
        DatabaseConstants.STUDENT_COURSES_QUERY, studentId));

    while (resultSet.next()) {
      int id = resultSet.getInt("course_id");
      String course = resultSet.getString("course_name");
      courses.add(id);

      System.out.println(id + ". " + course);
    }
  }

  private void deleteCourse(Statement statement, int studentId, int courseId)
    throws SQLException {
    statement.executeUpdate(String.format("DELETE FROM students_courses\n"
        + "WHERE student_id = %d AND course_id = %d;", studentId, courseId));

    System.out.println("Course deleted successfully");
  }
}
