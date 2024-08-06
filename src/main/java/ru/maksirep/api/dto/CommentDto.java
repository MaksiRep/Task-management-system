package ru.maksirep.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CommentDto(

        @NotNull
        @Schema(description = "Идентификатор комментария",
                example = "1")
        int id,

        @NotNull
        @Schema(description = "Идентификатор задачи, к которой написан комментарий",
                example = "1")
        int taskId,

        @NotNull
        @Schema(description = "Идентификатор автора комментария",
                example = "1")
        int commentAuthorId,

        @NotNull
        @Schema(description = "Текст комментария",
                example = "Текст комментария")
        String text) {
}
