package com.foxminded.courses;

import java.io.IOException;
import java.sql.*;

public class Main {

    public static void main(String[] args) throws SQLException, IOException {

        new Menu().displayMenu(Constants.URL, Constants.USER, Constants.PASSWORD);
    }
}
