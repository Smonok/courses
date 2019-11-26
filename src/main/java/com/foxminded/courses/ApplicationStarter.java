package com.foxminded.courses;

public class ApplicationStarter {

    public static void startApplication() {
        TablesFiller filler = new TablesFiller(DataSourceCustomizer.customizeDataSource());

        filler.fillAllTables();
        Menu.workWithApplication();
    }

    private ApplicationStarter() {
        throw new IllegalStateException();
    }
}
