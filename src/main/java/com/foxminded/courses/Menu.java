package com.foxminded.courses;

import static org.slf4j.LoggerFactory.getLogger;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.slf4j.Logger;

public class Menu {
    private static final String WRONG_INPUT_MESSAGE = "Error! The entered number is not match any of the proposed";
    private static final String EXPECTED_INTEGER_MESSAGE = "Expected integer";
    private static final Scanner scanner = new Scanner(System.in);
    private static final DataController controller = new DataController();
    private static final DataPrinter printer = new DataPrinter();
    private static final Logger LOG = getLogger(Menu.class);
    private static boolean isExit = false;

    static void workWithApplication(Statement statement) {
        displayMenu();
        workWithInput(statement);
        if (!isExit) {
            LOG.info("Work with the database was finished");
        }
    }

    private static void displayMenu() {
        LOG.info("1. Enter 1 to find all groups with less or equals students count");
        LOG.info("2. Enter 2 to find all students related to course with given name");
        LOG.info("3. Enter 3 to add new student");
        LOG.info("4. Enter 4 to delete student by student id");
        LOG.info("5. Enter 5 to add a student to the course");
        LOG.info("6. Enter 6 to remove the student from one of his or her courses");
        LOG.info("7. Enter 7 for exit");
    }

    private static void workWithInput(Statement statement) {
        LOG.info("You choose: ");

        try {
            int input = scanner.nextInt();
            switch (input) {
                case (1):
                    printGroupsWithStudentsCount(statement);
                    break;
                case (2):
                    printStudentsRelatedToCourse(statement);
                    break;
                case (3):
                    addStudentByEnteredData(statement);
                    break;
                case (4):
                    deleteStudentById(statement);
                    break;
                case (5):
                    addStudentToCourse(statement);
                    break;
                case (6):
                    removeStudentFromCourse(statement);
                    break;
                case (7):
                    LOG.info("Work with the database was finished");
                    isExit = true;
                    break;
                default:
                    startFromTheBeginning(statement);
                    break;
            }
        } catch (SQLException e) {
            LOG.error("SQLException: {}", e.getMessage());
        } catch (InputMismatchException e1) {
            LOG.error(EXPECTED_INTEGER_MESSAGE, e1);
        }
    }

    private static void printGroupsWithStudentsCount(Statement statement) throws SQLException {
        int studentsNumber = enterNumberToCompareStudents();
        int lessOrEquals = chooseHowCompareGroups(studentsNumber);

        if (lessOrEquals == 1 || lessOrEquals == 2) {
            printer.printGroupsAndStudentsNumber(statement, studentsNumber, lessOrEquals);
        } else {
            startFromTheBeginning(statement);
        }
    }

    private static int enterNumberToCompareStudents() {
        LOG.info("Enter the number with which you will compare the number of students: ");

        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            throw new InputMismatchException(EXPECTED_INTEGER_MESSAGE);
        }
    }

    private static int chooseHowCompareGroups(int number) {
        LOG.info("-> Enter 1 if you want to find groups with students count less then {}", number);
        LOG.info("-> Enter 2 if you want to find groups with students count that equals {}", number);
        LOG.info("-> You choose: ");

        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            throw new InputMismatchException(EXPECTED_INTEGER_MESSAGE);
        }

    }

    private static void printStudentsRelatedToCourse(Statement statement) throws SQLException {
        int courseId = enterCourseIdFromList(statement);

        if (TablesUtil.isCourseExists(courseId)) {
            printer.printStudentsRelatedToCourse(statement, courseId);
        } else {
            startFromTheBeginning(statement);
        }
    }

    private static void addStudentByEnteredData(Statement statement) throws SQLException {
        String firstName = enterName("first");
        scanner.nextLine();
        String lastName = enterName("last");
        int groupId = enterGroupIdFromList(statement);

        controller.addStudent(statement, firstName, lastName, groupId);
    }

    private static String enterName(String nameNumber) {
        LOG.info("Enter student's {} name: ", nameNumber);
        return scanner.nextLine();
    }

    private static int enterGroupIdFromList(Statement statement) throws SQLException {
        printer.printAllGroups(statement);

        LOG.info("Enter student's group: ");
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            throw new InputMismatchException(EXPECTED_INTEGER_MESSAGE);
        }
    }

    private static void deleteStudentById(Statement statement) throws SQLException {
        int sudentId = enterStudentIdFromList(statement);

        if (TablesUtil.isStudentExists(sudentId)) {
            controller.deleteStudentById(statement, sudentId);
        } else {
            startFromTheBeginning(statement);
        }
    }

    private static void addStudentToCourse(Statement statement) throws SQLException {
        int studentId = enterStudentIdFromList(statement);

        if (TablesUtil.isStudentExists(studentId)) {
            int courseId = enterCourseIdFromList(statement);

            if (TablesUtil.isCourseExists(courseId)) {
                controller.addCourseToStudent(statement, studentId, courseId);
            } else {
                startFromTheBeginning(statement);
            }
        } else {
            startFromTheBeginning(statement);
        }
    }

    private static int enterCourseIdFromList(Statement statement) throws SQLException {
        printer.printAllCourses(statement);
        LOG.info("Enter the course id: ");
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            throw new InputMismatchException(EXPECTED_INTEGER_MESSAGE);
        }
    }

    private static void removeStudentFromCourse(Statement statement) throws SQLException {
        int studentId = enterStudentIdFromList(statement);

        if (TablesUtil.isStudentExists(studentId)) {
            printer.printCoursesByStudentId(statement, studentId);

            LOG.info("Enter course id: ");

            try {
                int course = scanner.nextInt();

                if (TablesUtil.isCourseExists(course)) {
                    controller.removeStudentFromCourse(statement, studentId, course);
                } else {
                    startFromTheBeginning(statement);
                }
            } catch (InputMismatchException e) {
                throw new InputMismatchException(EXPECTED_INTEGER_MESSAGE);
            }
        } else {
            startFromTheBeginning(statement);
        }
    }

    private static int enterStudentIdFromList(Statement statement) throws SQLException {
        printer.printAllStudents(statement);
        LOG.info("Enter student id: ");
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            throw new InputMismatchException(EXPECTED_INTEGER_MESSAGE);
        }
    }

    private static void startFromTheBeginning(Statement statement) {
        LOG.info(WRONG_INPUT_MESSAGE);
        LOG.info("Try again");
        displayMenu();
        workWithInput(statement);
    }

    private Menu() {
        throw new IllegalStateException();
    }
}
