package ru.maksirep.core.enums;

public enum Priority {

    HIGH("высокий"),

    MIDDLE("средний"),

    LOW("низкий");

    private final String value;

    Priority(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
