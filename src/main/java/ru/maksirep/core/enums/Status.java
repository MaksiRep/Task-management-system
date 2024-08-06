package ru.maksirep.core.enums;

public enum Status {

    WAITING("в ожидании"),

    IN_PROGRESS("в процессе"),

    DONE("завершено");

    private final String value;

    Status (String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
