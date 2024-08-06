package ru.maksirep.api.dto.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        Фильтр на получение задач
        AUTHOR - получить задачи по автору
        EXECUTOR - получить задачи по исполнителю
        """)
public enum UsersTypeDto {

    AUTHOR("автор"),

    EXECUTOR("исполнитель");

    private final String value;

    UsersTypeDto(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
