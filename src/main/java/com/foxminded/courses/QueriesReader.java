package com.foxminded.courses;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

import org.slf4j.Logger;

public class QueriesReader {
    private static final ClassLoader loader = QueriesReader.class.getClassLoader();
    private static final Logger LOG = getLogger(QueriesReader.class);
    private boolean isTableFound = false;
    private int tableBeginIndex;
    private int tableEndIndex;

    public String createTable(String fileName, String tableName) {
        File sql = initializeFile(fileName);
        List<String> sqlLines = null;

        try {
            sqlLines = new ArrayList<>(Files.readAllLines(Paths.get(sql.getAbsolutePath())));
        } catch (IOException e) {
            LOG.error("Cannot read {} file", fileName);
        }
        StringJoiner query = new StringJoiner("\n");

        findTableBeginIndex(sqlLines, tableName);
        findTableEndIndex(sqlLines);
        if (!isTableFound) {
            final String exceptionMessage = String.format("Table %s not found in '%s'", tableName, fileName);
            throw new NoSuchElementException(exceptionMessage);
        }

        if(sqlLines != null) {
            for (int i = tableBeginIndex; i <= tableEndIndex; i++) {
                query.add(sqlLines.get(i));
            }
        }

        return query.toString();
    }

    private File initializeFile(String fileName) {
        URL url = Objects.requireNonNull(loader.getResource(fileName));
        return new File(url.getFile());
    }

    private void findTableBeginIndex(List<String> sqlLines, String tableName) {
        final int tableNameIndex = 2;

        if(sqlLines != null) {
            sqlLines.stream()
                .filter(line -> line.toUpperCase().contains("TABLE"))
                    .forEach(line -> {
                            String[] startQueryWords = line.split(DatabaseConstants.SPACES_OR_BRACKET);
                            String queryTableName = startQueryWords[tableNameIndex];

                            fillTableBeginIndexIfTableFound(tableName, queryTableName, sqlLines.indexOf(line));
                        });
        }
    }

    private void fillTableBeginIndexIfTableFound(String tableName, String queryTableName, int currentLineIndex) {
        if (tableName.equals(queryTableName)) {
            tableBeginIndex = currentLineIndex;
            isTableFound = true;
        }
    }

    private void findTableEndIndex(List<String> sqlLines) {
        if(sqlLines != null) {
            for (int i = tableBeginIndex; i < sqlLines.size(); i++) {
                if (sqlLines.get(i).contains(";")) {
                    tableEndIndex = i;
                    return;
                }
            }
        }
    }
}
