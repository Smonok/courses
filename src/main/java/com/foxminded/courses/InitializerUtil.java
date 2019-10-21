package com.foxminded.courses;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class InitializerUtil {
  public static final String URL = "jdbc:postgresql://localhost/courses";
  public static final String USER = "postgres";
  public static final String PASSWORD = "32147";

  private static Connection connection = null;
  private static Statement statement = null;

  public static void startApp(String url, String user, String password) {
    connectToDatabase(url, user, password);

    try {
      TablesFillerUtil.fillAllTables(statement);
    } catch (SQLException | IOException e1) {
      e1.printStackTrace();
    }

    new Menu().displayMenu(statement);

    try {
      statement.close();
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static void connectToDatabase(String url, String user,
    String password) {
    try {
      connection = DriverManager.getConnection(url, user, password);
      statement = connection.createStatement();
    } catch (SQLException e1) {
      e1.printStackTrace();
    }
  }
}
