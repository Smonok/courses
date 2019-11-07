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

public class QueriesReader {
    private final ClassLoader loader = QueriesReader.class.getClassLoader();
    private boolean isTableFound = false;
    private int tableBeginIndex;
    private int tableEndIndex;

    public String readTableСreationQuery(String fileName, String tableName) {
        File sql = initializeFile(fileName);
        List<String> sqlLines = null;
        try {
            sqlLines = new ArrayList<>(Files.readAllLines(Paths.get(sql.getAbsolutePath())));
        } catch (IOException e) {
            InitializerUtil.log.error("Cannot read %s file", fileName);
        }
        StringJoiner query = new StringJoiner("\n");

        findTableBeginIndex(sqlLines, tableName);
        findTableEndIndex(sqlLines);
        if (!isTableFound) {
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
        final int tableNameIndex = 2;

        sqlLines.stream()
            .filter(line -> line.toUpperCase().contains("TABLE"))
                .forEach(line -> {
                        String[] startQueryWords = line.split(DatabaseConstants.SPACES_OR_BRACKET);

                        if (tableName.equals(startQueryWords[tableNameIndex])) {
                            tableBeginIndex = sqlLines.indexOf(line);
                            isTableFound = true;
                            return;
                        }
                    });
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
