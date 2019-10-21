package com.foxminded.courses;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class QueriesReader {
  private final ClassLoader loader = QueriesReader.class.getClassLoader();
  private Boolean isTableFound = false;
  private int tableBeginIndex;

  public String readCreateTableQuery(String fileName, String tableName)
    throws IOException {
    File sql = initializeFile(fileName);
    List<String> sqlLines = new ArrayList<>(Files.readAllLines(Paths.get(sql
        .getAbsolutePath())));
    StringJoiner query = new StringJoiner("\n");

    findTableBeginIndex(sqlLines, tableName);
    if (!isTableFound) {
      return "";
    }

    for (int i = tableBeginIndex; i < sqlLines.size(); i++) {
      query.add(sqlLines.get(i));
      if (sqlLines.get(i).contains(";")) {
        break;
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

    sqlLines.stream().filter(line -> line.toUpperCase().contains("TABLE"))
        .forEach(line -> {
          String[] startQueryWords = line.split(
              DatabaseConstants.SPACES_OR_BRACKET);

          if (tableName.equals(startQueryWords[tableNameIndex])) {
            tableBeginIndex = sqlLines.indexOf(line);
            isTableFound = true;
            return;
          }
        });
  }
}
