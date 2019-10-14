package com.foxminded.courses;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Menu {
    private final Scanner scanner = new Scanner(System.in);
    private ResultSet set;

    public void displayMenu(String url, String user, String password) throws SQLException, IOException {
        Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();
        new TablesFiller().fillAllTables(statement);

        System.out.println("MENU");

        System.out.println("1. Enter 1 to find all groups with less or equals student count");
        System.out.println("2. Enter 2 to find all students related to course with given name");
        System.out.println("3. Enter 3 to add new student");
        System.out.println("4. Enter 4 to delete student by STUDENT_ID");
        System.out.println("5. Enter 5 to add a student to the course");
        System.out.println("5. Enter 6 to remove the student from one of his or her courses");

        System.out.print("You choose: ");
        int input = scanner.nextInt();

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
        }

        statement.close();
        connection.close();
        System.out.println("Work with the database is finished");
    }

    private void printGroupsAndStudentsNumber(Statement statement) throws SQLException {
        int number = getNumberToCompareStudents();
        int input = findGroupsWithLessOrEqualsStudentsNumber(number);

        set = statement.executeQuery(findGroupsRegardingToStudentsNumber(input, number));

        System.out.println("group_name | students count");
        while (set.next()) {
            String groupName = set.getString("group_name");
            int count = set.getInt("count");

            System.out.println(String.format("%-6s   %6d", groupName, count));
        }
    }

    private int getNumberToCompareStudents() {
        System.out.print("Enter the number with which you will compare the number of students: ");

        return scanner.nextInt();
    }

    private int findGroupsWithLessOrEqualsStudentsNumber(int number) {
        System.out.println("-> Enter 1 if you want to find groups with students count less then " + number);
        System.out.println("-> Enter 2 if you want to find groups with students count that equals " + number);
        System.out.println("-> You choose: ");

        return scanner.nextInt();
    }

    private String findGroupsRegardingToStudentsNumber(int input, int studentsNumber) {
        if (input == 1) {
            return String.format(Constants.GROUPS_WITH_STUDENTS_COUNT_QUERY, '<', studentsNumber);
        } else if (input == 2) {
            return String.format(Constants.GROUPS_WITH_STUDENTS_COUNT_QUERY, '=', studentsNumber);
        }

        return "";
    }

    private void printStudentsRelatedToCourse(Statement statement) throws SQLException {
        printAllCourses();
        int courseId = getCourseId();
        String courseName = getCourseNameById(statement, courseId);

        System.out.println("Students related to " + courseName + " course:");
        printStudents(statement, String.format(Constants.STUDENTS_RELATED_TO_COURSE_QUERY, courseId));
    }

    private String getCourseNameById(Statement statement, int courseId) throws SQLException {
        set = statement.executeQuery("SELECT * FROM courses WHERE course_id = " + courseId);
        set.next();

        return set.getString("course_name");
    }

    private void addStudent(Statement statement) throws SQLException {
        String firstName = getName("first");
        scanner.nextLine();
        String lastName = getName("last");
        int group = getGroupId();
        int studentId = Constants.STUDENTS_NUMBER;

        statement.executeUpdate(String.format(Constants.ADD_STUDENT_QUERY, ++studentId, firstName, lastName, group));

        System.out.println("Student added successfully");
    }

    private String getName(String nameNumber) {
        System.out.print("Enter student's " + nameNumber + " name: ");
        return scanner.nextLine();
    }

    private int getGroupId() {
        System.out.print("Enter student's group: ");
        return scanner.nextInt();
    }

    private void deleteStudentById(Statement statement) throws SQLException {
        System.out.println("All students:");
        printStudents(statement, Constants.SELECT_ALL_STUDENTS_QUERY);

        System.out.print("Enter id of the student which you want to delete: ");
        int studentId = scanner.nextInt();

        statement.executeUpdate(String.format("DELETE FROM students WHERE student_id = %d;", studentId));

        System.out.println("Student successfully deleted");
    }

    private void addCourseToStudent(Statement statement) throws SQLException {
        System.out.println("All students:");
        printStudents(statement, Constants.SELECT_ALL_STUDENTS_QUERY);

        System.out.print("Enter id of the student for which you want to add a course: ");
        int studentId = scanner.nextInt();

        printAllCourses();

        System.out.print("Enter the course which you want to add: ");
        int courseId = getCourseId();

        statement.executeUpdate(String
                .format("INSERT INTO students_courses(student_id, course_id) VALUES(%d, %d)", studentId, courseId));

        System.out.print("Course added successfully");
    }

    private void printAllCourses() {
        System.out.println("List of courses:");
        int i = 0;
        for (String course : Constants.COURSE_NAMES) {
            System.out.println(++i + ". " + course);
        }
    }

    private int getCourseId() {
        System.out.print("Enter the course id: ");

        return scanner.nextInt();
    }

    private void removeStudentFromCourse(Statement statement) throws SQLException {
        System.out.println("All students:");
        printStudents(statement, Constants.SELECT_ALL_STUDENTS_QUERY);

        System.out.print("Enter id of the student which you choose: ");
        int studentId = scanner.nextInt();

        printOneStudentCourses(statement, studentId);

        System.out.print("Enter id of a course which you want to delete: ");
        int courseId = scanner.nextInt();
        deleteCourse(statement, studentId, courseId);
    }

    private void printStudents(Statement statement, final String query) throws SQLException {
        set = statement.executeQuery(query);

        while (set.next()) {
            int id = set.getInt("student_id");
            String firstName = set.getString("first_name");
            String lastName = set.getString("last_name");

            System.out.println(id + ". " + firstName + " " + lastName);
        }
    }

    private void printOneStudentCourses(Statement statement, int studentId) throws SQLException {
        System.out.println("His (her) courses:");

        set = statement.executeQuery(String.format(Constants.STUDENT_COURSES_QUERY, studentId));

        while (set.next()) {
            int id = set.getInt("course_id");
            String course = set.getString("course_name");

            System.out.println(id + ". " + course);
        }
    }

    private void deleteCourse(Statement statement, int studentId, int courseId) throws SQLException {
        statement.executeUpdate(
                String.format("DELETE FROM students_courses\n" + "WHERE student_id = %d AND course_id = %d;",
                        studentId,
                        courseId));
        System.out.println("Course deleted successfully");
    }
}
