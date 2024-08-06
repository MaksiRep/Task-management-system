package ru.maksirep.api.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.jdbc.Sql;
import ru.maksirep.IntegrationTest;
import ru.maksirep.api.ApiPaths;
import ru.maksirep.api.dto.LoginRequest;
import ru.maksirep.api.error.ErrorResponse;

import java.util.stream.Stream;

class AuthorizeControllerTest extends IntegrationTest {

    @ParameterizedTest
    @MethodSource
    @Sql({"/scripts/authorize/test_users_authorize.sql"})
    void correctLogin(LoginRequest loginRequest, int expectedCode) {
        int responseCode = requestLogin(loginRequest, Object.class);
        Assertions.assertEquals(expectedCode, responseCode);
    }

    @ParameterizedTest
    @MethodSource
    @Sql({"/scripts/authorize/test_users_authorize.sql"})
    void incorrectLogin(LoginRequest loginRequest, int expectedCode) {
        int responseCode = requestLogin(loginRequest, ErrorResponse.class);
        Assertions.assertEquals(expectedCode, responseCode);
    }

    private static Stream<Arguments> correctLogin() {
        return Stream.of(
                Arguments.of(new LoginRequest("email1@mail.ru", "test1"), 200),
                Arguments.of(new LoginRequest("email2@mail.ru", "test2"), 200)
        );
    }

    private static Stream<Arguments> incorrectLogin() {
        return Stream.of(
                Arguments.of(new LoginRequest("email1@mail.ru", "test"), 404),
                Arguments.of(new LoginRequest(null, null), 400)
        );
    }

    private int requestLogin(LoginRequest loginRequest, Class<?> clazz) {
        return webTestClient.post()
                .uri(ApiPaths.LOGIN)
                .bodyValue(loginRequest)
                .exchange()
                .expectBody(clazz)
                .returnResult()
                .getStatus()
                .value();
    }

}
