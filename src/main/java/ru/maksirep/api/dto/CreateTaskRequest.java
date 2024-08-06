package ru.maksirep.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import ru.maksirep.core.enums.Priority;
import ru.maksirep.core.enums.Status;

public record CreateTaskRequest(

        @NotNull
        @Schema(description = "Заголовок задачи",
                example = "Заголовок")
        String header,

        @NotNull
        @Schema(description = "Описание задачи",
                example = "Описание")
        String description,

        @NotNull
        @Schema(description = "Статус задачи",
                example = "WAITING")
        Status status,

        @NotNull
        @Schema(description = "Приоритет задачи",
                example = "LOW")
        Priority priority,

        @NotNull
        @Schema(description = "Идентификатор исполнителя задачи",
                example = "1")
        int executorId) {
}
