package com.foxminded.courses.util;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

import org.slf4j.Logger;

public final class QueriesReaderUtil {
    private static final String SPACES_OR_BRACKET = "\\s+|\\(";
    private static final ClassLoader loader = QueriesReaderUtil.class.getClassLoader();
    private static final Logger LOG = getLogger(QueriesReaderUtil.class);
    private static boolean isTableExists;
    private static int tableBeginIndex;
    private static int tableEndIndex;

    private QueriesReaderUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String createTable(String fileName, String tableName) {
        List<String> sqlLines = readFile(fileName);

        isTableExists = false;
        findTableBeginIndex(sqlLines, tableName);
        findTableEndIndex(sqlLines);

        if (!isTableExists) {
            throw new NoSuchElementException("Table " + tableName + " not found in '" + fileName + "'");
        }

        StringJoiner query = new StringJoiner("\n");
        for (int i = tableBeginIndex; i <= tableEndIndex; i++) {
            query.add(sqlLines.get(i));
        }

        return query.toString();
    }

    private static List<String> readFile(String fileName){
        File sql = initializeFile(fileName);
        try {
           return Files.readAllLines(Paths.get(sql.getAbsolutePath()));
        } catch (IOException e) {
            LOG.error("Cannot read {} file", fileName, e);
        }

        return Collections.emptyList();
    }

    private static File initializeFile(String fileName) {
        URL url = Objects.requireNonNull(loader.getResource(fileName));
        return new File(url.getFile());
    }

    private static void findTableBeginIndex(List<String> sqlLines, String tableName) {
        final int tableNameIndex = 2;

        sqlLines.stream()
            .filter(line -> line.toUpperCase().contains("TABLE") && !isTableExists)
                .forEach(line -> {
                    String[] startQueryWords = line.split(SPACES_OR_BRACKET);
                    String queryTableName = startQueryWords[tableNameIndex];

                    checkIsTableExists(tableName, queryTableName);
                    saveCurrentLineIndex(sqlLines.indexOf(line));
                });
    }

    private static void saveCurrentLineIndex(int currentLineIndex) {
        if (isTableExists) {
            tableBeginIndex = currentLineIndex;
        }
    }

    private static void checkIsTableExists(String tableName, String queryTableName) {
        isTableExists = tableName.equals(queryTableName);
    }

    private static void findTableEndIndex(List<String> sqlLines) {
        for (int i = tableBeginIndex; i < sqlLines.size(); i++) {
            if (sqlLines.get(i).contains(";")) {
                tableEndIndex = i;
                return;
            }
        }
    }
}
