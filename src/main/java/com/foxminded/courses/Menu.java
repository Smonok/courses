package com.foxminded.courses;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Menu {
    private final Scanner scanner = new Scanner(System.in);
    private ResultSet resultSet;
    private final String WRONG_INPUT_MESSAGE = "Error! The entered number does not match any of the proposed";
    private boolean isExit = false;
    private static final StudentsController controller = new StudentsController();
    private final Logger LOG = LoggerFactory.getLogger(Menu.class);

    void workWithApplication(Statement statement) {
        displayMenu();
        workWithInput(statement);
        if (!isExit) {
            System.out.println("Work with the database was finished");
        }
    }

    private void displayMenu() {
        System.out.println("1. Enter 1 to find all groups with less or equals students count");
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
                    printGroupsAndStudentsNumberByEnteredData(statement);
                    break;
                case (2):
                    printStudentsRelatedToCourseByEnteredCourse(statement);
                    break;
                case (3):
                    addStudentByEnteredData(statement);
                    break;
                case (4):
                    deleteStudentByEnteredId(statement);
                    break;
                case (5):
                    addCourseToStudentByEnteredData(statement);
                    break;
                case (6):
                    removeStudentFromCourseByEnteredData(statement);
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
            LOG.error("SQLException: " + e.getMessage());
        } catch (InputMismatchException e1) {
            LOG.error(e1 + ": expected integer!");
        }
    }

    private void printGroupsAndStudentsNumberByEnteredData(Statement statement) throws SQLException {
        int studentsNumber = enterNumberToCompareStudents();
        int lessOrEquals = chooseHowCompareGroups(statement, studentsNumber);

        if (lessOrEquals == 1 || lessOrEquals == 2) {
            controller.printGroupsAndStudentsNumber(statement, studentsNumber, lessOrEquals);
        } else {
            startFromTheBeginning(statement);
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

        return input;
    }

    private void printStudentsRelatedToCourseByEnteredCourse(Statement statement) throws SQLException {
        int courseId = enterCourseIdFromList(statement);

        if (TablesFillerUtil.doesCourseExists(courseId)) {
            controller.printStudentsRelatedToCourse(statement, courseId);
        } else {
            startFromTheBeginning(statement);
        }
    }

    private void addStudentByEnteredData(Statement statement) throws SQLException {
        String firstName = enterName("first");
        scanner.nextLine();
        String lastName = enterName("last");
        int groupId = enterGroupIdFromList(statement);

        controller.addStudent(statement, firstName, lastName, groupId);
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
            printParsedGroupsInfo(resultSet);
        } catch (SQLException e) {
            LOG.error("Cannot print all groups.", e);
            throw e;
        }
    }

    private void printParsedGroupsInfo(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            String groupName = resultSet.getString("group_name");
            int id = resultSet.getInt("group_id");

            System.out.println(id + ". " + groupName);
        }
    }

    private void deleteStudentByEnteredId(Statement statement) throws SQLException {
        int sudentId = enterStudentIdFromList(statement);

        if (TablesFillerUtil.doesStudentExists(sudentId)) {
            controller.deleteStudentById(statement, sudentId);
        } else {
            startFromTheBeginning(statement);
        }
    }

    private void addCourseToStudentByEnteredData(Statement statement) throws SQLException {
        int studentId = enterStudentIdFromList(statement);

        if (TablesFillerUtil.doesStudentExists(studentId)) {
            int courseId = enterCourseIdFromList(statement);

            if (TablesFillerUtil.doesCourseExists(courseId)) {
                controller.addCourseToStudent(statement, studentId, courseId);
            } else {
                startFromTheBeginning(statement);
            }
        } else {
            startFromTheBeginning(statement);
        }
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
            printParsedCoursesInfo(resultSet);
        } catch (SQLException e) {
            LOG.error("Cannot print all courses.", e);
            throw e;
        }
    }

    private void removeStudentFromCourseByEnteredData(Statement statement) throws SQLException {
        int studentId = enterStudentIdFromList(statement);

        if (TablesFillerUtil.doesStudentExists(studentId)) {
            printCoursesByStudentId(statement, studentId);

            System.out.print("Enter course id: ");
            int course = scanner.nextInt();

            if (TablesFillerUtil.doesCourseExists(course)) {
                controller.removeStudentFromCourse(statement, studentId, course);
            } else {
                startFromTheBeginning(statement);
            }
        } else {
            startFromTheBeginning(statement);
        }
    }

    private int enterStudentIdFromList(Statement statement) throws SQLException {
        printAllStudents(statement);
        System.out.print("Enter student id: ");
        return scanner.nextInt();
    }

    private void printAllStudents(Statement statement) throws SQLException {
        try {
            resultSet = statement.executeQuery(DatabaseConstants.ALL_STUDENTS);
            printParsedStudentsInfo(resultSet);
        } catch (SQLException e) {
            LOG.error("Cannot print all students.", e);
            throw e;
        }
    }

    private void printParsedStudentsInfo(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            int id = resultSet.getInt("student_id");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");

            System.out.println(id + ". " + firstName + " " + lastName);
        }
    }

    private void printCoursesByStudentId(Statement statement, int studentId) throws SQLException {
        System.out.println("His (her) courses:");

        String studentCourses = String.format(DatabaseConstants.STUDENT_COURSES, studentId);
        resultSet = statement.executeQuery(studentCourses);

        printParsedCoursesInfo(resultSet);
    }

    private void printParsedCoursesInfo(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            int id = resultSet.getInt("course_id");
            String name = resultSet.getString("course_name");

            System.out.println(id + ". " + name);
        }
    }

    private void startFromTheBeginning(Statement statement) {
        System.out.println(WRONG_INPUT_MESSAGE);
        System.out.println("Try again");
        displayMenu();
        workWithInput(statement);
    }
}
