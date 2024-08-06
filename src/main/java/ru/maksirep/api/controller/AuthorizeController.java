package ru.maksirep.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.maksirep.api.ApiPaths;
import ru.maksirep.api.dto.JwtResponse;
import ru.maksirep.api.dto.LoginRequest;
import ru.maksirep.core.service.AuthorizeService;

@Tag(name = "Авторизация")
@Validated
@RestController
public class AuthorizeController {

    private final AuthorizeService authorizeService;

    public AuthorizeController(AuthorizeService authorizeService) {
        this.authorizeService = authorizeService;
    }

    @Operation(summary = "Авторизация пользователя в системе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает jwt для авторизации"),
            @ApiResponse(responseCode = "404", description = "Профиль не существует в бд",
                    content = @Content(schema = @Schema(hidden = true)))})
    @PostMapping(ApiPaths.LOGIN)
    public JwtResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        return authorizeService.login(loginRequest);
    }
}
