package com.foxminded.courses;

public final class Constants {
    public static final String URL = "jdbc:postgresql://localhost/courses";
    public static final String USER = "postgres";
    public static final String PASSWORD = "32147";

    public static final int GROUPS_NUMBER = 10;
    public static final int COURSES_NUMBER = 10;
    public static final int STUDENTS_NUMBER = 200;

    public static final String[] FIRST_NAMES = { "Jacob", "Michael", "Matthew", "Joshua", "Christopher", "Nicholas",
            "Andrew", "Joseph", "Daniel", "Tyler", "William", "Brandon", "Ryan", "John", "Zachary", "David", "Anthony",
            "James", "Justin", "Alexander" };
    public static final String[] LAST_NAMES = { "Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis",
            "Garcia", "Rodriguez", "Wilson", "Martinez", "Anderson", "Taylor", "Thomas", "Hernandez", "Moore", "Martin",
            "Jackson", "Thompson", "White" };
    public static final String[] COURSE_NAMES = { "math", "biology", "chemistry", "programming", "history", "geography",
            "music", "art", "algebra", "geometry" };

    public static final String SPACES_OR_BRACKET = "\\s+|\\(";

    public static final String GROUPS_WITH_STUDENTS_COUNT_QUERY = "SELECT students.group_id, group_name, COUNT(*) as count\n"
            + "FROM  students\n" + "LEFT JOIN groups ON students.group_id = groups.group_id\n"
            + "GROUP BY students.group_id, group_name\n" + "HAVING COUNT(*) %c %d;";
    public static final String STUDENTS_RELATED_TO_COURSE_QUERY = "SELECT students_courses.student_id, first_name, last_name\n"
            + "FROM students_courses\n" + "LEFT JOIN students ON students_courses.student_id = students.student_id\n"
            + "LEFT JOIN courses ON students_courses.course_id = courses.course_id\n"
            + "GROUP BY students_courses.student_id, courses.course_id, first_name, last_name\n"
            + "HAVING courses.course_id = %d;";
    public static final String ADD_STUDENT_QUERY = "INSERT INTO students(student_id, first_name, last_name, group_id)\n"
            + "VALUES\n" + "(%d, '%s', '%s', %d);";
    public static final String STUDENT_COURSES_QUERY = "SELECT students_courses.course_id, course_name\n"
            + "FROM students_courses\n" + "LEFT JOIN courses ON students_courses.course_id = courses.course_id\n"
            + "GROUP BY students_courses.course_id, course_name,  students_courses.student_id\n"
            + "HAVING students_courses.student_id = %d;";
    public static final String SELECT_ALL_STUDENTS_QUERY = "SELECT * FROM students";

    private Constants() {
        throw new AssertionError();
    }
}
