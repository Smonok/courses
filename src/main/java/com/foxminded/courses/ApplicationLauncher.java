package com.foxminded.courses;

import static com.foxminded.courses.DataSourceConfig.getDataSource;

public class ApplicationLauncher {

    public static void startApplication() {
        TablesInitializer filler = new TablesInitializer(getDataSource());

        filler.initTables();
        Menu.workWithApplication();
    }

    private ApplicationLauncher() {
        throw new IllegalStateException();
    }
}
