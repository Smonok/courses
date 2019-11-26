package com.foxminded.courses;

import static com.foxminded.courses.DataSourceCustomizer.customizeDataSource;
import static org.slf4j.LoggerFactory.getLogger;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.slf4j.Logger;

public class Menu {
    private static final String WRONG_INPUT_MESSAGE = "Error! Wrong input";
    private static final String EXPECTED_INTEGER_MESSAGE = "Expected integer";

    private static final Scanner scanner = new Scanner(System.in);
    private static final DBDataHandler handler = new DBDataHandler(customizeDataSource());
    private static final DBDataSelector selector = new DBDataSelector(customizeDataSource());
    private static final Logger LOG = getLogger(Menu.class);

    private static boolean isExit = false;

    public static void workWithApplication() {
        startMenu();
        if (!isExit) {
            LOG.info("Work with the database was finished");
        }
    }

    private static void startMenu() {
        LOG.info("1. Enter 1 to find all groups with less or equals students count");
        LOG.info("2. Enter 2 to find all students related to course with given name");
        LOG.info("3. Enter 3 to add new student");
        LOG.info("4. Enter 4 to delete student by student id");
        LOG.info("5. Enter 5 to add a student to the course");
        LOG.info("6. Enter 6 to remove the student from one of his or her courses");
        LOG.info("7. Enter 7 for exit");

        workWithInput();
    }

    private static void workWithInput() {
        LOG.info("You choose: ");

        try {
            int input = scanner.nextInt();
            switch (input) {
                case (1):
                    printGroupsWithStudentsCount();
                    break;
                case (2):
                    printStudentsByCourse();
                    break;
                case (3):
                    addStudent();
                    break;
                case (4):
                    deleteStudentById();
                    break;
                case (5):
                    addStudentToCourse();
                    break;
                case (6):
                    removeStudentFromCourse();
                    break;
                case (7):
                    LOG.info("Work with the database was finished");
                    isExit = true;
                    break;
                default:
                    startFromTheBeginning();
                    break;
            }
        } catch (SQLException e) {
            LOG.error("SQLException: {}", e.getMessage(), e);
        } catch (InputMismatchException e1) {
            LOG.error(EXPECTED_INTEGER_MESSAGE, e1);
        }
    }

    private static void printGroupsWithStudentsCount() throws SQLException {
        int studentsNumber = enterNumberToCompareStudents();
        int lessOrEquals = chooseHowCompareGroups(studentsNumber);

        if (lessOrEquals == 1 || lessOrEquals == 2) {
            LOG.info(selector.selectGroupsWithStudentsNumber(studentsNumber, lessOrEquals));
        } else {
            startFromTheBeginning();
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

    private static void printStudentsByCourse() throws SQLException {
        int courseId = enterCourseIdFromList();

        if (handler.isCourseExists(courseId)) {
            LOG.info(selector.selectStudentsByCourse(courseId));
        } else {
            startFromTheBeginning();
        }
    }

    private static void addStudent() throws SQLException {
        String firstName = enterName();
        scanner.nextLine();
        String lastName = enterSurname();
        int groupId = enterGroupIdFromList();

        handler.addStudent(firstName, lastName, groupId);
    }

    private static String enterName() {
        LOG.info("Enter student's first name: ");
        return scanner.nextLine();
    }

    private static String enterSurname() {
        LOG.info("Enter student's last name: ");
        return scanner.nextLine();
    }

    private static int enterGroupIdFromList() throws SQLException {
        LOG.info(selector.selectAllGroups());

        LOG.info("Enter student's group: ");
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            throw new InputMismatchException(EXPECTED_INTEGER_MESSAGE);
        }
    }

    private static void deleteStudentById() throws SQLException {
        int studentId = enterStudentIdFromList();

        if (handler.isStudentExists(studentId)) {
            handler.deleteStudentById(studentId);
        } else {
            startFromTheBeginning();
        }
    }

    private static void addStudentToCourse() throws SQLException {
        int studentId = enterStudentIdFromList();

        if (handler.isStudentExists(studentId)) {
            LOG.info(selector.selectCoursesByStudentId(studentId));

            int courseId = enterCourseIdFromList();
            if (!handler.isStudentHasCourse(studentId, courseId)) {
                handler.addCourseToStudent(studentId, courseId);
            } else {
                LOG.info("Error! This student already has choosen course");
                startFromTheBeginning();
            }
        } else {
            startFromTheBeginning();
        }
    }

    private static int enterCourseIdFromList() throws SQLException {
        LOG.info(selector.selectAllCourses());
        LOG.info("Enter the course id: ");
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            throw new InputMismatchException(EXPECTED_INTEGER_MESSAGE);
        }
    }

    private static void removeStudentFromCourse() throws SQLException {
        int studentId = enterStudentIdFromList();

        if (handler.isStudentExists(studentId)) {
            LOG.info(selector.selectCoursesByStudentId(studentId));

            LOG.info("Enter course id: ");

            try {
                int courseId = scanner.nextInt();

                if (handler.isStudentHasCourse(studentId, courseId)) {
                    handler.removeStudentFromCourse(studentId, courseId);
                } else {
                    startFromTheBeginning();
                }
            } catch (InputMismatchException e) {
                throw new InputMismatchException(EXPECTED_INTEGER_MESSAGE);
            }
        } else {
            startFromTheBeginning();
        }
    }

    private static int enterStudentIdFromList() throws SQLException {
        LOG.info(selector.selectAllStudents());
        LOG.info("Enter student id: ");
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            throw new InputMismatchException(EXPECTED_INTEGER_MESSAGE);
        }
    }

    private static void startFromTheBeginning() {
        LOG.info(WRONG_INPUT_MESSAGE);
        LOG.info("Try again");
        startMenu();
    }

    private Menu() {
        throw new IllegalStateException();
    }
}
