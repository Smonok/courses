package com.foxminded.courses;

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

public class QueriesReader {
    private static final String SPACES_OR_BRACKET = "\\s+|\\(";
    private static final ClassLoader loader = QueriesReader.class.getClassLoader();
    private static final Logger LOG = getLogger(QueriesReader.class);
    private boolean isTableFound;
    private int tableBeginIndex;
    private int tableEndIndex;

    public String createTable(String fileName, String tableName) {
        List<String> sqlLines = readFile(fileName);

        findTableBeginIndex(sqlLines, tableName);
        findTableEndIndex(sqlLines);

        if (!isTableFound) {
            throw new NoSuchElementException("Table " + tableName + " not found in '" + fileName + "'");
        }

        StringJoiner query = new StringJoiner("\n");
        for (int i = tableBeginIndex; i <= tableEndIndex; i++) {
            query.add(sqlLines.get(i));
        }

        return query.toString();
    }

    private List<String> readFile(String fileName){
        File sql = initializeFile(fileName);
        try {
           return Files.readAllLines(Paths.get(sql.getAbsolutePath()));
        } catch (IOException e) {
            LOG.error("Cannot read {} file", fileName, e);
        }

        return Collections.emptyList();
    }

    private File initializeFile(String fileName) {
        URL url = Objects.requireNonNull(loader.getResource(fileName));
        return new File(url.getFile());
    }

    private void findTableBeginIndex(List<String> sqlLines, String tableName) {
        final int tableNameIndex = 2;

        sqlLines.stream()
            .filter(line -> line.toUpperCase().contains("TABLE") && !isTableFound)
                .forEach(line -> {
                    String[] startQueryWords = line.split(SPACES_OR_BRACKET);
                    String queryTableName = startQueryWords[tableNameIndex];

                    checkIfTableFound(tableName, queryTableName);
                    saveCurrentLineIndex(sqlLines.indexOf(line));
                });
    }

    private void saveCurrentLineIndex(int currentLineIndex) {
        if (isTableFound) {
            tableBeginIndex = currentLineIndex;
        }
    }

    private void checkIfTableFound(String tableName, String queryTableName) {
        isTableFound = tableName.equals(queryTableName);
    }

    private void findTableEndIndex(List<String> sqlLines) {
        for (int i = tableBeginIndex; i < sqlLines.size(); i++) {
            if (sqlLines.get(i).contains(";")) {
                tableEndIndex = i;
                return;
            }
        }
    }
}
