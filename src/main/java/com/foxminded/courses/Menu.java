package com.foxminded.courses;

import static com.foxminded.courses.config.DataSourceConfig.getDataSource;
import static org.slf4j.LoggerFactory.getLogger;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.slf4j.Logger;

import com.foxminded.courses.db.dao.CoursesDao;
import com.foxminded.courses.db.dao.GroupsDao;
import com.foxminded.courses.db.dao.StudentsDao;

public class Menu {
    private static final String WRONG_INPUT_MESSAGE = "Error! Wrong input";
    private static final String EXPECTED_INTEGER_MESSAGE = "Please, enter the number";

    private static final Scanner scanner = new Scanner(System.in);
    private static final GroupsDao groupsDao = new GroupsDao(getDataSource());
    private static final StudentsDao studentsDao = new StudentsDao(getDataSource());
    private static final CoursesDao coursesDao = new CoursesDao(getDataSource());
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
            LOG.info(groupsDao.selectGroupsByStudentsNumber(studentsNumber, lessOrEquals));
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

        if (coursesDao.isCourseExists(courseId)) {
            LOG.info(studentsDao.selectStudentsByCourse(courseId));
        } else {
            startFromTheBeginning();
        }
    }

    private static void addStudent() throws SQLException {
        String firstName = enterName();
        String lastName = enterSurname();
        int groupId = enterGroupIdFromList();

        studentsDao.addStudent(firstName, lastName, groupId);
    }

    private static String enterName() {
        LOG.info("Enter student's first name: ");
        scanner.nextLine();
        return scanner.nextLine();
    }

    private static String enterSurname() {
        LOG.info("Enter student's last name: ");
        return scanner.nextLine();
    }

    private static int enterGroupIdFromList() throws SQLException {
        LOG.info(groupsDao.selectAllGroups());

        LOG.info("Enter student's group: ");
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            throw new InputMismatchException(EXPECTED_INTEGER_MESSAGE);
        }
    }

    private static void deleteStudentById() throws SQLException {
        int studentId = enterStudentIdFromList();

        if (studentsDao.isStudentExists(studentId)) {
            studentsDao.deleteStudentById(studentId);
        } else {
            startFromTheBeginning();
        }
    }

    private static void addStudentToCourse() throws SQLException {
        int studentId = enterStudentIdFromList();

        if (studentsDao.isStudentExists(studentId)) {
            LOG.info(coursesDao.selectCoursesByStudentId(studentId));

            int courseId = enterCourseIdFromList();
            if (!coursesDao.isStudentHasCourse(studentId, courseId)) {
                coursesDao.addStudentToCourse(studentId, courseId);
            } else {
                LOG.info("Error! Student with id = {} is already in {} course.", studentId, courseId);
                startFromTheBeginning();
            }
        } else {
            startFromTheBeginning();
        }
    }

    private static int enterCourseIdFromList() throws SQLException {
        LOG.info(coursesDao.selectAllCourses());
        LOG.info("Enter the course id: ");
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            throw new InputMismatchException(EXPECTED_INTEGER_MESSAGE);
        }
    }

    private static void removeStudentFromCourse() throws SQLException {
        int studentId = enterStudentIdFromList();

        if (studentsDao.isStudentExists(studentId)) {
            LOG.info(coursesDao.selectCoursesByStudentId(studentId));

            LOG.info("Enter course id: ");

            try {
                int courseId = scanner.nextInt();

                if (coursesDao.isStudentHasCourse(studentId, courseId)) {
                    coursesDao.removeStudentFromCourse(studentId, courseId);
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
        LOG.info(studentsDao.selectAllStudents());
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
