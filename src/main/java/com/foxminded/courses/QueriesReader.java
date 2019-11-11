package com.foxminded.courses;

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
import org.slf4j.LoggerFactory;

public class QueriesReader {
    private final ClassLoader loader = QueriesReader.class.getClassLoader();
    private boolean isTableFound = false;
    private int tableBeginIndex;
    private int tableEndIndex;
    private final Logger LOG = LoggerFactory.getLogger(QueriesReader.class);

    public String readTableСreationQuery(String fileName, String tableName) {
        File sql = initializeFile(fileName);
        List<String> sqlLines = null;
        try {
            sqlLines = new ArrayList<>(Files.readAllLines(Paths.get(sql.getAbsolutePath())));
        } catch (IOException e) {
            LOG.error("Cannot read " + fileName + " file");
        }
        StringJoiner query = new StringJoiner("\n");

        findTableBeginIndex(sqlLines, tableName);
        findTableEndIndex(sqlLines);
        if (!isTableFound) {
            LOG.error("NoSuchElementException: Table " + tableName + " not found in '" + fileName + "'");
            throw new NoSuchElementException("Table " + tableName + " not found in '" + fileName + "'");
        }

        for (int i = tableBeginIndex; i <= tableEndIndex; i++) {
            query.add(sqlLines.get(i));
        }

        return query.toString();
    }

    private File initializeFile(String fileName) {
        URL url = Objects.requireNonNull(loader.getResource(fileName));

        return new File(url.getFile());
    }

    private void findTableBeginIndex(List<String> sqlLines, String tableName) {
        final int queryTableNameIndex = 2;

        sqlLines.stream()
            .filter(line -> line.toUpperCase().contains("TABLE"))
                .forEach(line -> {
                        String[] startQueryWords = line.split(DatabaseConstants.SPACES_OR_BRACKET);
                        String queryTableName = startQueryWords[queryTableNameIndex];

                        fillTableBeginIndexIfTableFound(tableName, queryTableName, sqlLines.indexOf(line));
                    });
    }

    private void fillTableBeginIndexIfTableFound(String tableName, String queryTableName, int currentLineIndex) {
        if (tableName.equals(queryTableName)) {
            tableBeginIndex = currentLineIndex;
            isTableFound = true;
        }
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
