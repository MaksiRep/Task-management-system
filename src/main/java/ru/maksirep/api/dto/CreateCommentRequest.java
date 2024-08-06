package ru.maksirep.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreateCommentRequest(

        @NotNull
        @Schema(description = "Идентификатор задачи, к которой будет написан комментарий",
                example = "1")
        int taskId,

        @NotNull
        @Schema(description = "Текст комментария",
                example = "Текст комментария")
        String text) {
}
