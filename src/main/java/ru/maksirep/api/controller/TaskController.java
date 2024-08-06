package ru.maksirep.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.maksirep.api.ApiPaths;
import ru.maksirep.api.dto.*;
import ru.maksirep.api.dto.enums.UsersTypeDto;
import ru.maksirep.core.enums.Priority;
import ru.maksirep.core.enums.Status;
import ru.maksirep.core.service.TaskService;

import java.util.List;

@Tag(name = "Задачи")
@Validated
@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Создание задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ничего не возвращает"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат одного из полей"),
            @ApiResponse(responseCode = "404", description = "Профиль исполнителя не существует в системе")})
    @PostMapping(ApiPaths.TASK)
    public void createTask(@Valid @RequestBody CreateTaskRequest createTaskRequest) {
        taskService.createTask(createTaskRequest);
    }

    @Operation(summary = "Получение задачи по ее идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает информацию о задаче"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат идентификатора",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Задача с введенным идентификатором не найдена в системе",
                    content = @Content(schema = @Schema(hidden = true)))})
    @GetMapping(ApiPaths.TASK_BY_ID)
    public TaskDto getTaskById(@PathVariable int id) {
        return taskService.getTaskById(id);
    }

    @Operation(summary = "Получение списка задач с фильтрами и пагинацией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает список задач"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат одного из полей",
                    content = @Content(schema = @Schema(hidden = true)))})
    @GetMapping(ApiPaths.TASK)
    public List<TaskDto> getTaskPagination(
            @RequestParam(required = false)
            @Parameter(
                    name = "page",
                    description = """
                            Номер страницы, которую необходимо получить
                            Если отправить отрицательное значение значение или null,
                            будет возвращена первая страница
                            """)
            Integer page,
            @Parameter(
                    name = "size",
                    description = """
                            Размер страницы
                            Если отправить отрицательное значение значение или null,
                            будет возвращено значение в 10 задач на страницу
                            """)
            @RequestParam(required = false)
            Integer size,
            @Parameter(
                    name = "header",
                    description = """
                            Заголовок задачи
                            Если отправить null, поле не будет принимать участия в фильтрации
                            """)
            @RequestParam(required = false)
            String header,
            @Parameter(
                    name = "description",
                    description = """
                            Описание задачи
                            Если отправить null, поле не будет принимать участия в фильтрации
                            """)
            @RequestParam(required = false)
            String description,
            @Parameter(
                    name = "status",
                    description = """
                            Статус задачи
                            Если отправить null, поле не будет принимать участия в фильтрации
                            """)
            @RequestParam(required = false)
            Status status,
            @Parameter(
                    name = "priority",
                    description = """
                            Приоритет задачи
                            Если отправить null, поле не будет принимать участия в фильтрации
                            """)
            @RequestParam(required = false)
            Priority priority,
            @Parameter(
                    name = "authorId",
                    description = """
                            Идентификатор автора задачи
                            Если отправить null, поле не будет принимать участия в фильтрации
                            """)
            @RequestParam(required = false)
            Integer authorId,
            @Parameter(
                    name = "executorId",
                    description = """
                            Идентификатор исполнителя задачи
                            Если отправить null, поле не будет принимать участия в фильтрации
                            """)
            @RequestParam(required = false)
            Integer executorId) {
        return taskService.getTaskPagination(page, size, header, description, status, priority, authorId, executorId);
    }

    @Operation(summary = """
            Обновление задачи (автором, исполнителем)
            Обращение по эндпоинту допустимо любым авторизованным пользователем,
            но если пользователь не является исполнителем/автором, то выбрасывается ошибка.
            При обращении на эндпоинт исполнителем с полями, помимо status, ничего не произойдет для использованных полей,
            в итоге изменится только status
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ничего не возвращает, изменяет сущность задачи"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат идентификатора"),
            @ApiResponse(responseCode = "403", description = "Попытка пользователем, не являющимся автором или исполнителем поменять сущность"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена в системе")})
    @PatchMapping(ApiPaths.TASK_BY_ID)
    public void updateTaskById(
            @Parameter(
                    name = "id",
                    description = """
                            Идентификатор изменяемой задачи
                            """)
            @PathVariable int id,
            @RequestBody UpdateTaskRequest updateTaskRequest) {
        taskService.updateTaskById(id, updateTaskRequest);
    }

    @Operation(summary = "Удаление задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = """
                    Ничего не возвращает, удаляет задачу
                    При наличии комментариев к задаче, полностью удаляет их из системы
                    """),
            @ApiResponse(responseCode = "400", description = "Некорректный формат идентификатора"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена в системе"),
            @ApiResponse(responseCode = "403", description = "Попытка пользователем, не являющимся автором или исполнителем поменять сущность")})
    @DeleteMapping(ApiPaths.TASK_BY_ID)
    public void deleteTaskById(@PathVariable int id) {
        taskService.deleteTask(id);
    }

    @Operation(summary = "Получение списка задач с фильтрами и пагинацией по пользователю (исполнителю, автору)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает список задач"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат одного из полей",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Пользователь с указанными идентификатором не найден в системе",
                    content = @Content(schema = @Schema(hidden = true)))})
    @GetMapping(ApiPaths.TASK_BY_USER)
    public List<TaskDto> getTasksByUser(
            @Parameter(
                    name = "userId",
                    description = """
                            Идентификатор автора или исполнителя задачи
                            """)
            @RequestParam
            int userId,
            @Parameter(
                    name = "usersTypeDto",
                    description = """
                            Поле, отвечающее за то, в качестве кого будет рассматриваться пользователь:
                            В качестве автора или исполнителя
                            """)
            @RequestParam
            UsersTypeDto usersTypeDto,
            @Parameter(
                    name = "page",
                    description = """
                            Номер страницы, которую необходимо получить
                            Если отправить отрицательное значение значение или null,
                            будет возвращена первая страница
                            """)
            @RequestParam(required = false)
            Integer page,
            @Parameter(
                    name = "size",
                    description = """
                            Размер страницы
                            Если отправить отрицательное значение значение или null,
                            будет возвращено значение в 10 задач на страницу
                            """)
            @RequestParam(required = false)
            Integer size,
            @Parameter(
                    name = "header",
                    description = """
                            Заголовок задачи
                            Если отправить null, поле не будет принимать участия в фильтрации
                            """)
            @RequestParam(required = false)
            String header,
            @Parameter(
                    name = "description",
                    description = """
                            Описание задачи
                            Если отправить null, поле не будет принимать участия в фильтрации
                            """)
            @RequestParam(required = false)
            String description,
            @Parameter(
                    name = "status",
                    description = """
                            Статус задачи
                            Если отправить null, поле не будет принимать участия в фильтрации
                            """)
            @RequestParam(required = false)
            Status status,
            @Parameter(
                    name = "priority",
                    description = """
                            Приоритет задачи
                            Если отправить null, поле не будет принимать участия в фильтрации
                            """)
            @RequestParam(required = false)
            Priority priority
    ) {
        return taskService.getTasksByUser(userId, usersTypeDto, page, size, header, description, status, priority);
    }

    @Operation(summary = "Создание комментария к задаче")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ничего не возвращает, создает комментарий"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат одного из полей"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена в системе")})
    @PostMapping(ApiPaths.COMMENT)
    public void createTaskComment(@Valid @RequestBody CreateCommentRequest createCommentRequest) {
        taskService.createComment(createCommentRequest);
    }

    @Operation(summary = "Получение списка комментариев к конкретной задаче")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ничего не возвращает, создает комментарий"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат одного из полей",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена в системе",
                    content = @Content(schema = @Schema(hidden = true)))})
    @GetMapping(ApiPaths.COMMENT)
    public List<CommentDto> getTaskComments(
            @Parameter(
                    name = "taskId",
                    description = """
                            Идентификатор задачи, по которой производится поиск комментариев
                            """)
            @RequestParam
            int taskId,
            @Parameter(
                    name = "commentAuthorId",
                    description = """
                            Идентификатор автора, который оставил комментарий к задаче
                            Если отправить null, поле не будет принимать участия в фильтрации
                            """)
            @RequestParam(required = false)
            Integer commentAuthorId,
            @Parameter(
                    name = "text",
                    description = """
                            Текст комментария
                            Если отправить null, поле не будет принимать участия в фильтрации
                            """)
            @RequestParam(required = false)
            String text,
            @Parameter(
                    name = "page",
                    description = """
                            Номер страницы, которую необходимо получить
                            Если отправить отрицательное значение значение или null,
                            будет возвращена первая страница
                            """)
            @RequestParam(required = false)
            Integer page,
            @Parameter(
                    name = "size",
                    description = """
                            Размер страницы
                            Если отправить отрицательное значение значение или null,
                            будет возвращено значение в 10 задач на страницу
                            """)
            @RequestParam(required = false)
            Integer size) {
        return taskService.getTaskComments(taskId, commentAuthorId, text, page, size);
    }

}
