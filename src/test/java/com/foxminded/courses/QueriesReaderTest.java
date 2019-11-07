package com.foxminded.courses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QueriesReaderTest {
    private PrintStream createTableFileWriter;
    private final ClassLoader loader = QueriesReaderTest.class.getClassLoader();
    private final String FILE_NAME = "create_test_tables_query.sql";
    private final String COURSES_TABLE_NAME = "courses";
    private final String GROUPS_TABLE_NAME = "groups";

    private final String CREATE_GROUPS_TABLE_QUERY = "CREATE TABLE groups \n" + "(  \n"
                    + "   group_id SERIAL PRIMARY KEY,\n" + "   group_name VARCHAR (20)\n" + ");";

    private final String CREATE_COURSES_TABLE_QUERY = "CREATE TABLE courses\n" + "(\n"
                    + "   course_id SERIAL PRIMARY KEY,\n" + "   course_name VARCHAR (20),\n"
                    + "   course_description TEXT\n" + ");";

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
    void readTableСreationQueryShouldThrowNullPointerExceptionWhenFileNotFound() {
        String incorrectFileName = "incorrect";

        assertThrows(NullPointerException.class, () -> new QueriesReader().readTableСreationQuery(incorrectFileName,
                        GROUPS_TABLE_NAME));
    }

    @Test
    void readTableСreationQueryShouldThrowNoSuchElementExceptionWhenEmptyFile() throws IOException {

        assertThrows(NoSuchElementException.class, () -> new QueriesReader().readTableСreationQuery(FILE_NAME,
                        GROUPS_TABLE_NAME));
    }

    @Test
    void readTableСreationQueryShouldThrowNoSuchElementExceptionWhenTableDoesNotExists() throws IOException {
        String tableName = "students";

        createTableFileWriter.println(CREATE_GROUPS_TABLE_QUERY);

        assertThrows(NoSuchElementException.class, () -> new QueriesReader().readTableСreationQuery(FILE_NAME,
                        tableName));
    }

    @Test
    void readTableСreationQueryShouldReturnQueryWhenTableExists() throws IOException {
        createTableFileWriter.println(CREATE_COURSES_TABLE_QUERY);

        String actualResult = new QueriesReader().readTableСreationQuery(FILE_NAME, COURSES_TABLE_NAME);

        assertEquals(CREATE_COURSES_TABLE_QUERY, actualResult);
    }

    @Test
    void readTableСreationQueryShouldReturnQueryWhenManySpaces() throws IOException {
        String expectedResult = "CREATE      TABLE       groups      \n" + "(  \n" + "   group_id SERIAL PRIMARY KEY,\n"
                        + ");";

        createTableFileWriter.println(expectedResult);

        String actualResult = new QueriesReader().readTableСreationQuery(FILE_NAME, GROUPS_TABLE_NAME);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void readTableСreationQueryShouldReturnQueryWhenAllInOneLine() throws IOException {
        String expectedResult = "CREATE TABLE groups(group_id SERIAL PRIMARY KEY);";

        createTableFileWriter.println(expectedResult);

        String actualResult = new QueriesReader().readTableСreationQuery(FILE_NAME, GROUPS_TABLE_NAME);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void readTableСreationQueryShouldReturnQueryWhenSeveralQueriesInTheFile() throws IOException {
        createTableFileWriter.println(CREATE_GROUPS_TABLE_QUERY);
        createTableFileWriter.println(CREATE_COURSES_TABLE_QUERY);

        String actualResult = new QueriesReader().readTableСreationQuery(FILE_NAME, COURSES_TABLE_NAME);

        assertEquals(CREATE_COURSES_TABLE_QUERY, actualResult);
    }

    @Test
    void readTableСreationQueryShouldReturnQueryWhenPreviousTableNameContainsCurrentTableName() throws IOException {
        String createTable = "CREATE TABLE students_courses\n" + "(\n" + "   student_id INTEGER NOT NULL,\n" + ");";

        createTableFileWriter.println(createTable);
        createTableFileWriter.println(CREATE_COURSES_TABLE_QUERY);

        String actualResult = new QueriesReader().readTableСreationQuery(FILE_NAME, COURSES_TABLE_NAME);

        assertEquals(CREATE_COURSES_TABLE_QUERY, actualResult);
    }
}
