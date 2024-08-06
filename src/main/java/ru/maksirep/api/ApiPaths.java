package ru.maksirep.api;

public class ApiPaths {

    private ApiPaths() {
    }

    public static final String LOGIN = "/api/login";
    public static final String TASK = "/api/task";
    public static final String TASK_BY_ID = TASK + "/{id}";
    public static final String TASK_BY_USER = TASK + "/by-user";
    public static final String COMMENT = TASK + "/comment";
}
