package ru.maksirep.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.maksirep.core.enums.Priority;
import ru.maksirep.core.enums.Status;

public record UpdateTaskRequest(

        @Schema(description = "Заголовок задачи, изменяется только автором, при попытке поменять исполнителем - ничего не произойдет",
                example = "Заголовок")
        String header,

        @Schema(description = "Описание задачи, изменяется только автором, при попытке поменять исполнителем - ничего не произойдет",
                example = "Описание")
        String description,

        @Schema(description = "Статус задачи, изменяется только автором, при попытке поменять исполнителем - ничего не произойдет",
                example = "WAITING")
        Status status,

        @Schema(description = "Приоритет задачи, изменяется автором и исполнителем",
                example = "LOW")
        Priority priority,

        @Schema(description = "Идентификатор исполнителя задачи, изменяется только автором, при попытке поменять исполнителем - ничего не произойдет",
                example = "1")
        Integer executorId) {
}
