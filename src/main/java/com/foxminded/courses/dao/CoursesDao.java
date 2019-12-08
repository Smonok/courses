package com.foxminded.courses.dao;

import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringJoiner;

import javax.sql.DataSource;

import org.slf4j.Logger;

import com.foxminded.courses.DatabaseConstants;

public class CoursesDao {
    private static final String COURSE_ID = "course_id";
    private static final String COURSE_NAME = "course_name";
    private static final String EXISTS = "is_exists";
    private static final Logger LOG = getLogger(CoursesDao.class);
    private final DataSource dataSource;

    public CoursesDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isCourseExists(int id) throws SQLException {
        String isExists = String.format(DatabaseConstants.IS_COURSE_EXISTS, id);

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(isExists)) {

            resultSet.next();
            return resultSet.getBoolean(EXISTS);
        } catch (SQLException e) {
            throw new SQLException("Cannot check if course with id = " + id + " exists\n" + isExists, e);
        }
    }

    public boolean isStudentHasCourse(int studentId, int courseId) throws SQLException {
        String isExists = String.format(DatabaseConstants.IS_STUDENT_COURSE_EXISTS, studentId, courseId);

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(isExists)) {

            resultSet.next();
            return resultSet.getBoolean(EXISTS);
        } catch (SQLException e) {
            throw new SQLException("Cannot check if student with id = " + studentId
                + " has course with id = " + courseId + "\n" + isExists, e);
        }
    }

    public String selectAllCourses() throws SQLException {
        LOG.info("List of courses:");

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(DatabaseConstants.ALL_COURSES);

            return combineCoursesInfo(resultSet);
        } catch (SQLException e) {
            throw new SQLException("Cannot select all courses.\n" + DatabaseConstants.ALL_COURSES, e);
        }
    }

    public String selectCoursesByStudentId(int studentId) throws SQLException {
        String studentCourses = String.format(DatabaseConstants.STUDENT_COURSES, studentId);

        LOG.info("Courses of student with ID = {}:", studentId);
        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(studentCourses)) {

            return combineCoursesInfo(resultSet);
        } catch (SQLException e) {
            throw new SQLException("Cannot select courses for student with Id = " + studentId + "\n"
                + studentCourses, e);
        }
    }

    private String combineCoursesInfo(ResultSet resultSet) throws SQLException {
        StringJoiner courses = new StringJoiner("\n");

        while (resultSet.next()) {
            courses.add(parseCourseInfo(resultSet));
        }

        return courses.toString();
    }

    private String parseCourseInfo(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(COURSE_ID);
        String name = resultSet.getString(COURSE_NAME);

        return String.format("%d. %s", id, name);
    }

    public void addStudentToCourse(int studentId, int courseId) throws SQLException {
        String addCourseForStudent = String.format(DatabaseConstants.INSERT_STUDENT_COURSE, studentId, courseId);

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()) {

            statement.executeUpdate(addCourseForStudent);
            LOG.info("Course with id = {} successfully added to student with id = {}", courseId, studentId);
        } catch (SQLException e) {
            throw new SQLException("Cannot add student with ID = " + studentId + " to " + courseId + " course\n"
                + addCourseForStudent, e);
        }
    }

    public void removeStudentFromCourse(int studentId, int courseId) throws SQLException {
        String deleteStudentCourse = String.format(DatabaseConstants.DELETE_STUDENT_FROM_COURSE, studentId, courseId);

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()) {

            statement.executeUpdate(deleteStudentCourse);
            LOG.info("Course with id = {} successfully deleted from student with id = {}", courseId, studentId);
        } catch (SQLException e) {
            throw new SQLException("Cannot remove student with ID = " + studentId + " from " + courseId + " course\n"
                + deleteStudentCourse, e);
        }
    }

    public String getCourseNameById(int courseId) throws SQLException {
        String courseName = String.format(DatabaseConstants.COURSE_NAME_BY_ID, courseId);

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(courseName)) {

            resultSet.next();
            return resultSet.getString(COURSE_NAME);
        } catch (SQLException e) {
            throw new SQLException("Cannot get course with ID = " + courseId + "\n" + courseName, e);
        }
    }
}
