package ru.maksirep.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import ru.maksirep.api.validation.Message;
import ru.maksirep.api.validation.RegularExpression;

public record LoginRequest(

        @NotNull
        @Pattern(regexp = RegularExpression.EMAIL, message = Message.INCORRECT_EMAIL)
        @Schema(description = "Email пользователя",
                example = "example@example.com")
        String email,

        @NotNull
        @Schema(description = "Пароль пользователя",
                example = "password")
        String password) {
}
