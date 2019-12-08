package com.foxminded.courses;

public final class DatabaseConstants {
    public static final int GROUPS_NUMBER = 10;
    public static final int COURSES_NUMBER = 10;
    public static final int STUDENTS_NUMBER = 200;

    public static final String GROUPS_WITH_STUDENTS_COUNT = "SELECT students.group_id, group_name, COUNT(*) as count\n"
                + "FROM  students\n"
                + "LEFT JOIN groups ON students.group_id = groups.group_id\n"
                + "GROUP BY students.group_id, group_name\n"
                + "HAVING COUNT(*) %c %d;";

    public static final String STUDENTS_BY_COURSE = "SELECT students_courses.student_id, first_name, last_name\n"
                + "FROM students_courses\n"
                + "LEFT JOIN students ON students_courses.student_id = students.student_id\n"
                + "LEFT JOIN courses ON students_courses.course_id = courses.course_id\n"
                + "GROUP BY students_courses.student_id, courses.course_id, first_name, last_name\n"
                + "HAVING courses.course_id = %d;";

    public static final String STUDENT_COURSES = "SELECT students_courses.course_id, course_name\n"
                + "FROM students_courses\n"
                + "LEFT JOIN courses ON students_courses.course_id = courses.course_id\n"
                + "GROUP BY students_courses.course_id, course_name,  students_courses.student_id\n"
                + "HAVING students_courses.student_id = %d;";

    public static final String ALL_STUDENTS = "SELECT student_id, first_name, last_name FROM students";

    public static final String ALL_GROUPS = "SELECT group_id, group_name FROM groups;";

    public static final String ALL_COURSES = "SELECT course_id, course_name FROM courses;";

    public static final String COURSE_NAME_BY_ID = "SELECT course_name FROM courses WHERE course_id = %d";


    public static final String IS_STUDENT_EXISTS = "SELECT EXISTS(SELECT student_id FROM students\n"
                + "WHERE student_id = %d) AS is_exists;";

    public static final String IS_GROUP_EXISTS = "SELECT EXISTS(SELECT group_id FROM groups\n"
                + "WHERE group_id = %d) AS is_exists;";

    public static final String IS_COURSE_EXISTS = "SELECT EXISTS(SELECT course_id FROM courses\n"
                + "WHERE course_id = %d) AS is_exists;";

    public static final String IS_STUDENT_COURSE_EXISTS = "SELECT EXISTS(SELECT student_id, course_id\n"
                + "FROM students_courses WHERE student_id = %d AND course_id = %d) AS is_exists;";


    public static final String INSERT_STUDENT = "INSERT INTO students(student_id, first_name, last_name, group_id)\n"
                + "VALUES\n" + "(%d, '%s', '%s', %d);";

    public static final String INSERT_GROUP = "INSERT INTO groups (group_id, group_name) VALUES (%d, '%s');";

    public static final String INSERT_COURSE = "INSERT INTO courses (course_id, course_name, course_description)\n"
                + "VALUES (%d, '%s', '%s');";

    public static final String INSERT_STUDENT_COURSE = "INSERT INTO students_courses(student_id, course_id)\n"
                + "VALUES(%d, %d)";


    public static final String DELETE_STUDENT_BY_ID = "DELETE FROM students WHERE student_id = %d;";

    public static final String DELETE_STUDENT_FROM_COURSE = "DELETE FROM students_courses\n"
                + "WHERE student_id = %d AND course_id = %d;";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS %s CASCADE;";


    public static final String[] FIRST_NAMES = {
                "Jacob", "Michael", "Matthew", "Joshua", "Christopher", "Nicholas", "Andrew", "Joseph", "Daniel",
                "Tyler", "William", "Brandon", "Ryan", "John", "Zachary", "David", "Anthony", "James", "Justin",
                "Alexander"
    };
    public static final String[] LAST_NAMES = {
                "Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia", "Rodriguez", "Wilson",
                "Martinez", "Anderson", "Taylor", "Thomas", "Hernandez", "Moore", "Martin", "Jackson", "Thompson",
                "White"
    };
    public static final String[] COURSE_NAMES = {
                "math", "biology", "chemistry", "programming", "history", "geography", "music", "art", "algebra",
                "geometry"
    };

    private DatabaseConstants() {
        throw new AssertionError();
    }
}
