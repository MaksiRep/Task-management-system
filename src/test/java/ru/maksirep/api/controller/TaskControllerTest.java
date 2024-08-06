package ru.maksirep.api.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.jdbc.Sql;
import ru.maksirep.IntegrationTest;
import ru.maksirep.api.ApiPaths;
import ru.maksirep.api.dto.*;
import ru.maksirep.api.dto.enums.UsersTypeDto;
import ru.maksirep.api.error.ErrorResponse;
import ru.maksirep.core.entity.Comment;
import ru.maksirep.core.entity.Task;
import ru.maksirep.core.enums.Priority;
import ru.maksirep.core.enums.Status;
import ru.maksirep.core.repository.CommentRepository;
import ru.maksirep.core.repository.TaskRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerTest extends IntegrationTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CommentRepository commentRepository;

    private final LoginRequest FIRST_USER_LOGIN_REQUEST = new LoginRequest("email1@mail.ru", "test1");

    @ParameterizedTest
    @MethodSource
    @Sql({"/scripts/task/create_task.sql"})
    void correctCreateTask(CreateTaskRequest createTaskRequest, Task expectedTask) {
        webTestClient.post()
                .uri(ApiPaths.TASK)
                .header("Authorization", getJwt(FIRST_USER_LOGIN_REQUEST))
                .bodyValue(createTaskRequest)
                .exchange()
                .expectBody(Void.class);
        Assertions.assertEquals(expectedTask, taskRepository.getTaskById(expectedTask.getId()).get());
    }

    private static Stream<Arguments> correctCreateTask() {
        return Stream.of(
                Arguments.of(new CreateTaskRequest("header", "description", Status.WAITING, Priority.LOW, 1),
                        new Task(1, "header", "description", Status.WAITING, Priority.LOW, 1, 1)),
                Arguments.of(new CreateTaskRequest("header", "description", Status.IN_PROGRESS, Priority.HIGH, 2),
                        new Task(2, "header", "description", Status.IN_PROGRESS, Priority.HIGH, 1, 2))
        );
    }

    @Test
    @Sql({"/scripts/task/create_task.sql"})
    void incorrectCreateTask() {
        int actualStatusCode = webTestClient.post()
                .uri(ApiPaths.TASK)
                .header("Authorization", getJwt(FIRST_USER_LOGIN_REQUEST))
                .bodyValue(new CreateTaskRequest("header", "description", Status.IN_PROGRESS, Priority.HIGH, 3))
                .exchange()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getStatus()
                .value();
        Assertions.assertEquals(404, actualStatusCode);
    }

    @Test
    @Sql({"/scripts/task/get_task_by_id.sql"})
    void correctGetTaskById() {
        TaskDto expectedTask = new TaskDto(1, "header", "description", "в ожидании", "низкий", 1, 1);
        TaskDto actualTask = webTestClient.get()
                .uri(builder -> builder.path(ApiPaths.TASK_BY_ID).build(1))
                .header("Authorization", getJwt(FIRST_USER_LOGIN_REQUEST))
                .exchange()
                .expectBody(TaskDto.class)
                .returnResult()
                .getResponseBody();
        Assertions.assertEquals(expectedTask, actualTask);
    }

    @Test
    @Sql({"/scripts/task/get_task_by_id.sql"})
    void incorrectGetTaskById() {
        int actualStatusCode = webTestClient.get()
                .uri(builder -> builder.path(ApiPaths.TASK_BY_ID).build(2))
                .header("Authorization", getJwt(FIRST_USER_LOGIN_REQUEST))
                .attribute("id", 2)
                .exchange()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getStatus()
                .value();
        Assertions.assertEquals(404, actualStatusCode);
    }

    @ParameterizedTest
    @MethodSource
    @Sql({"/scripts/task/get_task_pagination.sql"})
    void getTaskPagination(Integer page,
                           Integer size,
                           String header,
                           String description,
                           Status status,
                           Priority priority,
                           Integer authorId,
                           Integer executorId,
                           List<TaskDto> expectedTaskList) {
        List<TaskDto> actualTaskList = webTestClient.get()
                .uri(builder -> builder.path(ApiPaths.TASK)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("header", header)
                        .queryParam("description", description)
                        .queryParam("status", status)
                        .queryParam("priority", priority)
                        .queryParam("authorId", authorId)
                        .queryParam("executorId", executorId)
                        .build())
                .header("Authorization", getJwt(FIRST_USER_LOGIN_REQUEST))
                .exchange()
                .expectBody(new ParameterizedTypeReference<List<TaskDto>>() {
                })
                .returnResult()
                .getResponseBody();
        Assertions.assertEquals(expectedTaskList, actualTaskList);
    }

    private static Stream<Arguments> getTaskPagination() {
        return Stream.of(
                Arguments.of(null, null, null, null, null, null, null, null, Arrays.asList(
                        new TaskDto(1, "header1", "description", "в ожидании", "низкий", 1, 1),
                        new TaskDto(2, "header2", "description", "в ожидании", "высокий", 1, 2),
                        new TaskDto(3, "header3", "NO", "в ожидании", "средний", 1, 2))),
                Arguments.of(0, 2, null, null, null, null, null, null, Arrays.asList(
                        new TaskDto(1, "header1", "description", "в ожидании", "низкий", 1, 1),
                        new TaskDto(2, "header2", "description", "в ожидании", "высокий", 1, 2))),
                Arguments.of(0, 2, null, null, null, null, null, 2, Arrays.asList(
                        new TaskDto(2, "header2", "description", "в ожидании", "высокий", 1, 2),
                        new TaskDto(3, "header3", "NO", "в ожидании", "средний", 1, 2))),
                Arguments.of(null, null, null, "description", null, Priority.HIGH, null, null, Arrays.asList(
                        new TaskDto(2, "header2", "description", "в ожидании", "высокий", 1, 2))),
                Arguments.of(2, null, null, null, null, null, null, null, Arrays.asList())
        );
    }

    @ParameterizedTest
    @MethodSource
    @Sql({"/scripts/task/update_task_by_id.sql"})
    void correctUpdateTaskById(int id, UpdateTaskRequest updateTaskRequest, LoginRequest loginRequest, Task expectedTask) {
        webTestClient.patch()
                .uri(builder -> builder.path(ApiPaths.TASK_BY_ID).build(id))
                .header("Authorization", getJwt(loginRequest))
                .bodyValue(updateTaskRequest)
                .exchange()
                .expectBody(Void.class);
        Assertions.assertEquals(expectedTask, taskRepository.getTaskById(expectedTask.getId()).get());
    }

    private static Stream<Arguments> correctUpdateTaskById() {
        return Stream.of(
                Arguments.of(1,
                        new UpdateTaskRequest("testHeader", null, null, null, null),
                        new LoginRequest("email1@mail.ru", "test1"),
                        new Task(1, "testHeader", "description", Status.WAITING, Priority.LOW, 1, 2)),
                Arguments.of(1,
                        new UpdateTaskRequest("testHeader", null, Status.DONE, Priority.HIGH, null),
                        new LoginRequest("email2@mail.ru", "test2"),
                        new Task(1, "header1", "description", Status.DONE, Priority.LOW, 1, 2))
        );
    }

    @ParameterizedTest
    @MethodSource
    @Sql({"/scripts/task/update_task_by_id.sql"})
    void incorrectUpdateTaskById(int id, UpdateTaskRequest updateTaskRequest, LoginRequest loginRequest, int expectedStatusCode) {
        int statusCode = webTestClient.patch()
                .uri(builder -> builder.path(ApiPaths.TASK_BY_ID).build(id))
                .header("Authorization", getJwt(loginRequest))
                .bodyValue(updateTaskRequest)
                .exchange()
                .expectBody(Void.class)
                .returnResult()
                .getStatus()
                .value();
        Assertions.assertEquals(expectedStatusCode, statusCode);
    }

    private static Stream<Arguments> incorrectUpdateTaskById() {
        return Stream.of(
                Arguments.of(2,
                        new UpdateTaskRequest("testHeader", null, null, null, null),
                        new LoginRequest("email3@mail.ru", "test3"),
                        404),
                Arguments.of(1,
                        new UpdateTaskRequest("testHeader", null, null, null, null),
                        new LoginRequest("email3@mail.ru", "test3"),
                        403)
        );
    }

    @Test
    @Sql({"/scripts/task/delete_task_by_id.sql"})
    void correctDeleteTaskById() {
        int statusCode = webTestClient.delete()
                .uri(builder -> builder.path(ApiPaths.TASK_BY_ID).build(1))
                .header("Authorization", getJwt(FIRST_USER_LOGIN_REQUEST))
                .exchange()
                .expectBody(Void.class)
                .returnResult()
                .getStatus()
                .value();
        Assertions.assertEquals(200, statusCode);
    }

    @ParameterizedTest
    @MethodSource
    @Sql({"/scripts/task/delete_task_by_id.sql"})
    void incorrectDeleteTaskById(int id, LoginRequest loginRequest, int expectedStatusCode) {
        int statusCode = webTestClient.delete()
                .uri(builder -> builder.path(ApiPaths.TASK_BY_ID).build(id))
                .header("Authorization", getJwt(loginRequest))
                .exchange()
                .expectBody(Void.class)
                .returnResult()
                .getStatus()
                .value();
        Assertions.assertEquals(expectedStatusCode, statusCode);
    }

    private static Stream<Arguments> incorrectDeleteTaskById() {
        return Stream.of(
                Arguments.of(2,
                        new LoginRequest("email2@mail.ru", "test2"),
                        404),
                Arguments.of(1,
                        new LoginRequest("email2@mail.ru", "test2"),
                        403)
        );
    }

    @ParameterizedTest
    @MethodSource
    @Sql({"/scripts/task/get_task_pagination.sql"})
    void correctGetTasksByUser(int userId,
                               UsersTypeDto usersTypeDto,
                               Integer page,
                               Integer size,
                               String header,
                               String description,
                               Status status,
                               Priority priority,
                               List<TaskDto> expectedTaskList) {
        List<TaskDto> actualTaskList = webTestClient.get()
                .uri(builder -> builder.path(ApiPaths.TASK_BY_USER)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("header", header)
                        .queryParam("description", description)
                        .queryParam("status", status)
                        .queryParam("priority", priority)
                        .queryParam("userId", userId)
                        .queryParam("usersTypeDto", usersTypeDto)
                        .build())
                .header("Authorization", getJwt(FIRST_USER_LOGIN_REQUEST))
                .exchange()
                .expectBody(new ParameterizedTypeReference<List<TaskDto>>() {
                })
                .returnResult()
                .getResponseBody();
        Assertions.assertEquals(expectedTaskList, actualTaskList);
    }

    private static Stream<Arguments> correctGetTasksByUser() {
        return Stream.of(
                Arguments.of(1, UsersTypeDto.AUTHOR, null, null, null, null, null, null, Arrays.asList(
                        new TaskDto(1, "header1", "description", "в ожидании", "низкий", 1, 1),
                        new TaskDto(2, "header2", "description", "в ожидании", "высокий", 1, 2),
                        new TaskDto(3, "header3", "NO", "в ожидании", "средний", 1, 2))),
                Arguments.of(1, UsersTypeDto.EXECUTOR, null, null, null, null, null, null, Arrays.asList(
                        new TaskDto(1, "header1", "description", "в ожидании", "низкий", 1, 1))),
                Arguments.of(2, UsersTypeDto.AUTHOR, null, null, null, null, null, null, Arrays.asList()),
                Arguments.of(2, UsersTypeDto.EXECUTOR, null, null, null, null, null, null, Arrays.asList(
                        new TaskDto(2, "header2", "description", "в ожидании", "высокий", 1, 2),
                        new TaskDto(3, "header3", "NO", "в ожидании", "средний", 1, 2))),
                Arguments.of(1, UsersTypeDto.AUTHOR, null, null, null, "NO", null, null, Arrays.asList(
                        new TaskDto(3, "header3", "NO", "в ожидании", "средний", 1, 2))),
                Arguments.of(2, UsersTypeDto.EXECUTOR, null, null, null, null, null, Priority.HIGH, Arrays.asList(
                        new TaskDto(2, "header2", "description", "в ожидании", "высокий", 1, 2)))
        );
    }

    @Test
    @Sql({"/scripts/task/get_task_pagination.sql"})
    void incorrectGetTasksByUser() {
        int statusCode = webTestClient.get()
                .uri(builder -> builder.path(ApiPaths.TASK_BY_USER)
                        .queryParam("page", "")
                        .queryParam("size", "")
                        .queryParam("header", "")
                        .queryParam("description", "")
                        .queryParam("status", "")
                        .queryParam("priority", "")
                        .queryParam("userId", 3)
                        .queryParam("usersTypeDto", UsersTypeDto.AUTHOR)
                        .build())
                .header("Authorization", getJwt(FIRST_USER_LOGIN_REQUEST))
                .exchange()
                .expectBody(Void.class)
                .returnResult()
                .getStatus()
                .value();
        Assertions.assertEquals(404, statusCode);
    }

    @ParameterizedTest
    @MethodSource
    @Sql({"/scripts/comment/create_task_comment.sql"})
    void correctCreateTaskComment(CreateCommentRequest createCommentRequest,
                                  LoginRequest loginRequest,
                                  Comment expectedComment) {
        webTestClient.post()
                .uri(ApiPaths.COMMENT)
                .header("Authorization", getJwt(loginRequest))
                .bodyValue(createCommentRequest)
                .exchange()
                .expectBody(Void.class);
        Assertions.assertEquals(expectedComment, commentRepository.getCommentById(expectedComment.getId()).get());
    }

    private static Stream<Arguments> correctCreateTaskComment() {
        return Stream.of(
                Arguments.of(
                        new CreateCommentRequest(1, "first comment"),
                        new LoginRequest("email1@mail.ru", "test1"),
                        new Comment(1, 1, 1, "first comment")),
                Arguments.of(
                        new CreateCommentRequest(1, "second comment"),
                        new LoginRequest("email2@mail.ru", "test2"),
                        new Comment(2, 1, 2, "second comment"))
        );
    }

    @Test
    @Sql({"/scripts/comment/create_task_comment.sql"})
    void incorrectCreateTaskComment() {
        int statusCode = webTestClient.post()
                .uri(ApiPaths.COMMENT)
                .header("Authorization", getJwt(FIRST_USER_LOGIN_REQUEST))
                .bodyValue(new CreateCommentRequest(2, "first comment"))
                .exchange()
                .expectBody(Void.class)
                .returnResult()
                .getStatus()
                .value();
        Assertions.assertEquals(404, statusCode);
    }

    @ParameterizedTest
    @MethodSource
    @Sql({"/scripts/comment/get_task_comments_pagination.sql"})
    void correctGetTaskComments(int taskId,
                                Integer commentAuthorId,
                                String text,
                                Integer page,
                                Integer size,
                                List<CommentDto> expectedCommentList) {
        List<CommentDto> actualCommentList = webTestClient.get()
                .uri(builder -> builder.path(ApiPaths.COMMENT)
                        .queryParam("taskId", taskId)
                        .queryParam("commentAuthorId", commentAuthorId)
                        .queryParam("text", text)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .header("Authorization", getJwt(FIRST_USER_LOGIN_REQUEST))
                .exchange()
                .expectBody(new ParameterizedTypeReference<List<CommentDto>>() {
                })
                .returnResult()
                .getResponseBody();
        Assertions.assertEquals(expectedCommentList, actualCommentList);
    }

    private static Stream<Arguments> correctGetTaskComments() {
        return Stream.of(
                Arguments.of(1, null, null, null, null, Arrays.asList(
                        new CommentDto(1, 1, 1, "text1"),
                        new CommentDto(2, 1, 2, "text2")
                )),
                Arguments.of(2, null, null, null, null, Arrays.asList(
                        new CommentDto(3, 2, 1, "text3"),
                        new CommentDto(4, 2, 2, "text4")
                )),
                Arguments.of(1, 1, null, null, null, Arrays.asList(
                        new CommentDto(1, 1, 1, "text1")
                )),
                Arguments.of(2, 1, null, null, null, Arrays.asList(
                        new CommentDto(3, 2, 1, "text3")
                ))
        );
    }


    private String getJwt(LoginRequest loginRequest) {
        JwtResponse jwtResponse = webTestClient.post()
                .uri(ApiPaths.LOGIN)
                .bodyValue(loginRequest)
                .exchange()
                .expectBody(JwtResponse.class)
                .returnResult()
                .getResponseBody();
        return String.format("Bearer %s", jwtResponse.jwt());
    }
}
