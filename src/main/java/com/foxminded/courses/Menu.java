package com.foxminded.courses;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private final Scanner scanner = new Scanner(System.in);
    private ResultSet resultSet;
    private final String WRONG_INPUT_MESSAGE = "Error! The entered number does not match any of the proposed";
    private boolean isExit = false;
    private final StudentsController controller = new StudentsController();

    public void displayMenu(Statement statement) {
        System.out.println("MENU");
        showMenuChoices();
        workWithInput(statement);
        if (!isExit) {
            System.out.println("Work with the database was finished");
        }
    }

    private void showMenuChoices() {
        System.out.println("1. Enter 1 to find all groups with less or equals student count");
        System.out.println("2. Enter 2 to find all students related to course with given name");
        System.out.println("3. Enter 3 to add new student");
        System.out.println("4. Enter 4 to delete student by student id");
        System.out.println("5. Enter 5 to add a student to the course");
        System.out.println("6. Enter 6 to remove the student from one of his or her courses");
        System.out.println("7. Enter 7 for exit");
    }

    private void workWithInput(Statement statement) {
        System.out.print("You choose: ");
        int input = 0;

        try {
            input = scanner.nextInt();
            switch (input) {
                case (1):
                    int studentsNumber = enterNumberToCompareStudents();
                    int lessOrEquals = chooseHowCompareGroups(statement, studentsNumber);

                    controller.printGroupsAndStudentsNumber(statement, studentsNumber, lessOrEquals);
                    break;
                case (2):
                    int courseId = enterCourseIdFromList(statement);

                    if (!startFromTheBeginning(!TablesFillerUtil.doesCourseExists(courseId), statement)) {
                        controller.printStudentsRelatedToCourse(statement, courseId);
                    }
                    break;
                case (3):
                    String firstName = enterName("first");
                    scanner.nextLine();
                    String lastName = enterName("last");
                    int groupId = enterGroupIdFromList(statement);

                    if (!startFromTheBeginning(!TablesFillerUtil.doesGroupExists(groupId), statement)) {
                        controller.addStudent(statement, firstName, lastName, groupId);
                    }
                    break;
                case (4):
                    int sudentId = enterStudentIdFromList(statement);

                    if (!startFromTheBeginning(!TablesFillerUtil.doesStudentExists(sudentId), statement)) {
                        controller.deleteStudentById(statement, sudentId);
                    }
                    break;
                case (5):
                    int studentId = enterStudentIdFromList(statement);

                    if (!startFromTheBeginning(!TablesFillerUtil.doesStudentExists(studentId), statement)) {
                        int course = enterCourseIdFromList(statement);
                        if (!startFromTheBeginning(!TablesFillerUtil.doesCourseExists(course), statement)) {
                            controller.addCourseToStudent(statement, studentId, course);
                        }
                    }
                    break;
                case (6):
                    int student = enterStudentIdFromList(statement);

                    if (!startFromTheBeginning(!TablesFillerUtil.doesStudentExists(student), statement)) {
                        List<Integer> courses = new ArrayList<>();
                        printCoursesByStudentId(statement, student, courses);

                        System.out.print("Enter course id: ");
                        int course = scanner.nextInt();

                        if (!startFromTheBeginning(!TablesFillerUtil.doesCourseExists(course), statement)) {
                            controller.removeStudentFromCourse(statement, student, course);
                        }
                    }
                    break;
                case (7):
                    System.out.println("Work with the database was finished");
                    isExit = true;
                    break;
                default:
                    System.out.println(WRONG_INPUT_MESSAGE);
                    System.out.println("Try again");
                    workWithInput(statement);
                    break;
            }
        } catch (SQLException e) {
            InitializerUtil.log.error("SQLException: " + e.getMessage());
        } catch (InputMismatchException e1) {
            InitializerUtil.log.error(e1 + ": expected integer!");
        }
    }

    private int enterNumberToCompareStudents() {
        System.out.print("Enter the number with which you will compare the number of students: ");
        return scanner.nextInt();
    }

    private int chooseHowCompareGroups(Statement statement, int number) {
        System.out.println("-> Enter 1 if you want to find groups with students count less then " + number);
        System.out.println("-> Enter 2 if you want to find groups with students count that equals " + number);
        System.out.print("-> You choose: ");
        int input = scanner.nextInt();

        startFromTheBeginning(input < 1 || input > 2, statement);

        return input;
    }

    private String enterName(String nameNumber) {
        System.out.print("Enter student's " + nameNumber + " name: ");
        return scanner.nextLine();
    }

    private int enterGroupIdFromList(Statement statement) throws SQLException {
        printAllGroups(statement);

        System.out.print("Enter student's group: ");
        return scanner.nextInt();
    }

    private void printAllGroups(Statement statement) throws SQLException {
        try {
            resultSet = statement.executeQuery(DatabaseConstants.ALL_GROUPS);
            while (resultSet.next()) {
                String groupName = resultSet.getString("group_name");
                int id = resultSet.getInt("group_id");

                System.out.println(id + ". " + groupName);
            }
        } catch (SQLException e) {
            InitializerUtil.log.error("Cannot print all groups.", e);
            throw new SQLException("Cannot print all groups.", e);
        }
    }

    private int enterStudentIdFromList(Statement statement) throws SQLException {
        printAllStudents(statement);
        System.out.print("Enter student id: ");
        return scanner.nextInt();
    }

    private int enterCourseIdFromList(Statement statement) throws SQLException {
        printAllCourses(statement);
        System.out.print("Enter the course id: ");
        return scanner.nextInt();
    }

    private void printAllCourses(Statement statement) throws SQLException {
        System.out.println("List of courses:");

        try {
            resultSet = statement.executeQuery(DatabaseConstants.ALL_COURSES);
            while (resultSet.next()) {
                int id = resultSet.getInt("course_id");
                String name = resultSet.getString("course_name");

                System.out.println(id + ". " + name);
            }
        } catch (SQLException e) {
            InitializerUtil.log.error("Cannot print all courses.", e);
            throw new SQLException("Cannot print all courses.", e);
        }
    }

    private void printAllStudents(Statement statement) throws SQLException {
        try {
            resultSet = statement.executeQuery(DatabaseConstants.ALL_STUDENTS);
            while (resultSet.next()) {
                int id = resultSet.getInt("student_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                System.out.println(id + ". " + firstName + " " + lastName);
            }
        } catch (SQLException e) {
            InitializerUtil.log.error("Cannot print all students.", e);
            throw new SQLException("Cannot print all students.", e);
        }
    }

    private void printCoursesByStudentId(Statement statement, int studentId, List<Integer> courses)
        throws SQLException {
        System.out.println("His (her) courses:");

        String studentCourses = String.format(DatabaseConstants.STUDENT_COURSES, studentId);
        resultSet = statement.executeQuery(studentCourses);

        while (resultSet.next()) {
            int id = resultSet.getInt("course_id");
            String course = resultSet.getString("course_name");
            courses.add(id);

            System.out.println(id + ". " + course);
        }
    }

    private boolean startFromTheBeginning(boolean wrongInputCondition, Statement statement) {
        if (wrongInputCondition) {
            System.out.println(WRONG_INPUT_MESSAGE);
            System.out.println("Try again");
            showMenuChoices();
            workWithInput(statement);
            return true;
        }

        return false;
    }
}
