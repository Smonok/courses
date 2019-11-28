package com.foxminded.courses.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.foxminded.courses.TablesInitializer;

class GroupsDaoTest {
    private static final String NEW_LINE = "[\\r\\n]+";
    private static final String WHITESPACES = "\\s+";
    private static final String ERROR_MESSAGE = "No groups found";
    private static final JdbcDataSource dataSource = new JdbcDataSource();
    private static GroupsDao groups;
    private static TablesInitializer initializer;

    @BeforeAll
    static void setUp() throws SQLException {
        dataSource.setURL("jdbc:h2:mem:estest;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");

        initializer = new TablesInitializer(dataSource);
        groups = new GroupsDao(dataSource);

        initializer.initGroupsTable();
    }

    @Test
    void selectGroupsWithStudentsNumberShouldSelectNumberOfStudentsLessThanEnteredWhenOne() throws SQLException {
        int studentsNumber = 30;
        int lessOrEquals = 1;

        String actualResult = groups.selectGroupsWithStudentsNumber(studentsNumber, lessOrEquals);

        String[] resultLines = actualResult.split(NEW_LINE);

        for (String line : resultLines) {
            String[] groupWithStudents = line.split(WHITESPACES);
            String students = groupWithStudents[groupWithStudents.length - 1];
            int count = Integer.parseInt(students);

            assertTrue(count < studentsNumber);
        }
    }

    @Test
    void selectGroupsWithStudentsNumberShouldReturnErrorMessageWhenGroupsNotFound() throws SQLException {
        int studentsNumber = 3;
        int lessOrEquals = 1;

        String actualResult = groups.selectGroupsWithStudentsNumber(studentsNumber, lessOrEquals);
        assertEquals(ERROR_MESSAGE, actualResult);

        lessOrEquals = 2;
        actualResult = groups.selectGroupsWithStudentsNumber(studentsNumber, lessOrEquals);
        assertEquals(ERROR_MESSAGE, actualResult);
    }

    @Test
    void selectGroupsWithStudentsNumberShouldSelectNumberOfStudentsEqualsToEnteredWhenTwo() throws SQLException {
        int studentsNumber = 23;
        int lessOrEquals = 2;

        final int lastCharactersNumber = 2;

        String actualResult = groups.selectGroupsWithStudentsNumber(studentsNumber, lessOrEquals);

        if (!actualResult.equals(ERROR_MESSAGE)) {
            String[] resultLines = actualResult.split(NEW_LINE);

            for (String line : resultLines) {
                String students = line.substring(line.length() - lastCharactersNumber);
                int count = Integer.parseInt(students);

                assertEquals(count, studentsNumber);
            }
        }
    }
}
