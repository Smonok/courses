package com.foxminded.courses;

import static com.foxminded.courses.config.DataSourceConfig.getDataSource;

import com.foxminded.courses.db.TablesInitializer;

public class ApplicationLauncher {

    private ApplicationLauncher() {
        throw new IllegalStateException();
    }

    public static void startApplication() {
        TablesInitializer initializer = new TablesInitializer(getDataSource());

        initializer.initTables();
        Menu.workWithApplication();
    }
}
