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
    private ResultSet resultSet;

    public CoursesDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isCourseExists(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery(String.format(DatabaseConstants.IS_COURSE_EXISTS, id));

            resultSet.next();
            return resultSet.getBoolean(EXISTS);
        } catch (SQLException e) {
            throw new SQLException("Cannot check if course with id = " + id + " exists", e);
        }
    }

    public boolean isStudentHasCourse(int studentId, int courseId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery(String.format(DatabaseConstants.IS_STUDENT_COURSE_EXISTS, studentId,
                        courseId));

            resultSet.next();
            return resultSet.getBoolean(EXISTS);
        } catch (SQLException e) {
            throw new SQLException("Cannot check if student with id = " + studentId + " has course with id = "
                        + courseId, e);
        }
    }

    public String selectAllCourses() throws SQLException {
        LOG.info("List of courses:");

        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery(DatabaseConstants.ALL_COURSES);

            return parseCoursesInfo();
        } catch (SQLException e) {
            throw new SQLException("Cannot select all courses.", e);
        }
    }

    public String selectCoursesByStudentId(int studentId) throws SQLException {
        LOG.info("Courses of student with ID = {}:", studentId);

        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            String studentCourses = String.format(DatabaseConstants.STUDENT_COURSES, studentId);
            resultSet = statement.executeQuery(studentCourses);

            return parseCoursesInfo();
        } catch (SQLException e) {
            throw new SQLException("Cannot select courses for student with Id = " + studentId, e);
        }
    }

    private String parseCoursesInfo() throws SQLException {
        StringJoiner courses = new StringJoiner("\n");

        while (resultSet.next()) {
            int id = resultSet.getInt(COURSE_ID);
            String name = resultSet.getString(COURSE_NAME);

            courses.add(String.format("%d. %s", id, name));
        }

        return courses.toString();
    }

    public void addStudentToCourse(int studentId, int courseId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            String addCourseForStudent = String.format(DatabaseConstants.INSERT_STUDENT_COURSE, studentId, courseId);

            statement.executeUpdate(addCourseForStudent);
            LOG.info("Course with id = {} successfully added to student with id = {}", courseId, studentId);
        } catch (SQLException e) {
            throw new SQLException("Cannot add student with ID = " + studentId + " to " + courseId + " course", e);
        }
    }

    public void removeStudentFromCourse(int studentId, int courseId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
            String deleteStudentCourse = String.format(DatabaseConstants.DELETE_STUDENT_FROM_COURSE, studentId,
                        courseId);

            statement.executeUpdate(deleteStudentCourse);
            LOG.info("Course with id = {} successfully deleted from student with id = {}", courseId, studentId);
        } catch (SQLException e) {
            throw new SQLException("Cannot remove student with ID = " + studentId + " from " + courseId + " course", e);
        }
    }
}
