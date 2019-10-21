package com.foxminded.courses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.Objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QueriesReaderTest {
  private PrintStream createTableFileWriter;
  private final ClassLoader loader = QueriesReaderTest.class.getClassLoader();
  private final String FILE_NAME = "create_test_tables_query.sql";

  private final String CREATE_GROUPS_TABLE_QUERY = "CREATE TABLE groups \n"
      + "(  \n" + "   group_id SERIAL PRIMARY KEY,\n"
      + "   group_name VARCHAR (20)\n" + ");";

  private final String CREATE_COURSES_TABLE_QUERY = "CREATE TABLE courses\n"
      + "(\n" + "   course_id SERIAL PRIMARY KEY,\n"
      + "   course_name VARCHAR (20),\n" + "   course_description TEXT\n"
      + ");";

  @BeforeEach
  void initialize() throws IOException {
    URL url = Objects.requireNonNull(loader.getResource(FILE_NAME));
    File file = new File(url.getFile());

    createTableFileWriter = new PrintStream(new FileOutputStream(file
        .getAbsolutePath()));
  }

  @AfterEach
  void close() {
    createTableFileWriter.close();
  }

  @Test
  void readCreateTableQueryShouldThrowNullPointerExceptionWhenFileNotFound() {
    String incorrectFileName = "incorrect";
    String tableName = "groups";

    assertThrows(NullPointerException.class, () -> new QueriesReader()
        .readCreateTableQuery(incorrectFileName, tableName));
  }

  @Test
  void readCreateTableQueryShouldReturnEmptyStringWhenEmptyFile()
    throws IOException {
    String tableName = "groups";
    String expectedResult = "";
    String actualResult = new QueriesReader().readCreateTableQuery(FILE_NAME,
        tableName);

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void readCreateTableQueryShouldReturnEmptyStringWhenTableDoesNotExists()
    throws IOException {
    String tableName = "students";

    createTableFileWriter.println(CREATE_GROUPS_TABLE_QUERY);

    String expectedResult = "";
    String actualResult = new QueriesReader().readCreateTableQuery(FILE_NAME,
        tableName);

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void readCreateTableQueryShouldReturnQueryWhenTableExists()
    throws IOException {
    String tableName = "courses";

    createTableFileWriter.println(CREATE_COURSES_TABLE_QUERY);

    String expectedResult = CREATE_COURSES_TABLE_QUERY;
    String actualResult = new QueriesReader().readCreateTableQuery(FILE_NAME,
        tableName);

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void readCreateTableQueryShouldReturnQueryWhenManySpaces()
    throws IOException {
    String tableName = "groups";
    String query = "CREATE      TABLE       groups      \n" + "(  \n"
        + "   group_id SERIAL PRIMARY KEY,\n" + ");";

    createTableFileWriter.println(query);

    String expectedResult = query;
    String actualResult = new QueriesReader().readCreateTableQuery(FILE_NAME,
        tableName);

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void readCreateTableQueryShouldReturnQueryWhenAllInOneLine()
    throws IOException {
    String tableName = "groups";
    String query = "CREATE TABLE groups(group_id SERIAL PRIMARY KEY);";

    createTableFileWriter.println(query);

    String expectedResult = query;
    String actualResult = new QueriesReader().readCreateTableQuery(FILE_NAME,
        tableName);

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void readCreateTableQueryShouldReturnQueryWhenSeveralQueriesInTheFile()
    throws IOException {
    String tableName = "courses";

    createTableFileWriter.println(CREATE_GROUPS_TABLE_QUERY);
    createTableFileWriter.println(CREATE_COURSES_TABLE_QUERY);

    String expectedResult = CREATE_COURSES_TABLE_QUERY;

    String actualResult = new QueriesReader().readCreateTableQuery(FILE_NAME,
        tableName);

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void readCreateTableQueryShouldReturnQueryWhenPreviousTableNameContainsCurrentTableName()
    throws IOException {
    String tableName = "courses";

    createTableFileWriter.println("CREATE TABLE students_courses\n" + "(\n"
        + "   student_id INTEGER NOT NULL,\n" + ");\n");

    createTableFileWriter.println(CREATE_COURSES_TABLE_QUERY);

    String expectedResult = CREATE_COURSES_TABLE_QUERY;

    String actualResult = new QueriesReader().readCreateTableQuery(FILE_NAME,
        tableName);

    assertEquals(expectedResult, actualResult);
  }
}
