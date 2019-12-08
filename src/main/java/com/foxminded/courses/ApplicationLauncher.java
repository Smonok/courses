package com.foxminded.courses;

import static com.foxminded.courses.config.DataSourceConfig.getDataSource;

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
