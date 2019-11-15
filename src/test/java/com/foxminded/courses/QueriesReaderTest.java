package com.foxminded.courses;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class QueriesReaderTest {
    private static final ClassLoader loader = QueriesReaderTest.class.getClassLoader();
    private static final String FILE_NAME = "create_test_tables_query.sql";
    private static final String COURSES_TABLE_NAME = "courses";
    private static final String GROUPS_TABLE_NAME = "groups";

    private static final String CREATE_GROUPS_TABLE_QUERY = "CREATE TABLE groups \n" + "(  \n"
                    + "   group_id SERIAL PRIMARY KEY,\n" + "   group_name VARCHAR (20)\n" + ");";

    private static final String CREATE_COURSES_TABLE_QUERY = "CREATE TABLE courses\n" + "(\n"
                    + "   course_id SERIAL PRIMARY KEY,\n" + "   course_name VARCHAR (20),\n"
                    + "   course_description TEXT\n" + ");";

    private static PrintStream createTableFileWriter;

    @BeforeEach
    void initialize() throws IOException {
        URL url = Objects.requireNonNull(loader.getResource(FILE_NAME));
        File file = new File(url.getFile());

        createTableFileWriter = new PrintStream(new FileOutputStream(file.getAbsolutePath()));
    }

    @AfterEach
    void close() {
        createTableFileWriter.close();
    }

    @Test
    void createTableShouldThrowNullPointerExceptionWhenFileNotFound() {
        String incorrectFileName = "incorrect";

        assertThrows(NullPointerException.class, () -> new QueriesReader().createTable(incorrectFileName,
                        GROUPS_TABLE_NAME));
    }

    @Test
    void createTableShouldThrowNoSuchElementExceptionWhenEmptyFile() throws IOException {

        assertThrows(NoSuchElementException.class, () -> new QueriesReader().createTable(FILE_NAME, GROUPS_TABLE_NAME));
    }

    @Test
    void createTableShouldThrowNoSuchElementExceptionWhenTableDoesNotExists() throws IOException {
        String tableName = "students";

        createTableFileWriter.println(CREATE_GROUPS_TABLE_QUERY);

        assertThrows(NoSuchElementException.class, () -> new QueriesReader().createTable(FILE_NAME, tableName));
    }

    @Test
    void createTableShouldReturnQueryWhenTableExists() throws IOException {
        createTableFileWriter.println(CREATE_COURSES_TABLE_QUERY);

        String actualResult = new QueriesReader().createTable(FILE_NAME, COURSES_TABLE_NAME);

        assertEquals(CREATE_COURSES_TABLE_QUERY, actualResult);
    }

    @Test
    void createTableShouldReturnQueryWhenManySpaces() throws IOException {
        String expectedResult = "CREATE      TABLE       groups      \n" + "(  \n"
                        + "   group_id SERIAL PRIMARY KEY,\n);";

        createTableFileWriter.println(expectedResult);

        String actualResult = new QueriesReader().createTable(FILE_NAME, GROUPS_TABLE_NAME);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void createTableShouldReturnQueryWhenAllInOneLine() throws IOException {
        String expectedResult = "CREATE TABLE groups(group_id SERIAL PRIMARY KEY);";

        createTableFileWriter.println(expectedResult);

        String actualResult = new QueriesReader().createTable(FILE_NAME, GROUPS_TABLE_NAME);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void createTableShouldReturnQueryWhenSeveralQueriesInTheFile() throws IOException {
        createTableFileWriter.println(CREATE_GROUPS_TABLE_QUERY);
        createTableFileWriter.println(CREATE_COURSES_TABLE_QUERY);

        String actualResult = new QueriesReader().createTable(FILE_NAME, COURSES_TABLE_NAME);

        assertEquals(CREATE_COURSES_TABLE_QUERY, actualResult);
    }

    @Test
    void createTableShouldReturnQueryWhenPreviousTableNameContainsCurrentTableName() throws IOException {
        String createTable = "CREATE TABLE students_courses\n" + "(\n" + "   student_id INTEGER NOT NULL,\n" + ");";

        createTableFileWriter.println(createTable);
        createTableFileWriter.println(CREATE_COURSES_TABLE_QUERY);

        String actualResult = new QueriesReader().createTable(FILE_NAME, COURSES_TABLE_NAME);

        assertEquals(CREATE_COURSES_TABLE_QUERY, actualResult);
    }
}
