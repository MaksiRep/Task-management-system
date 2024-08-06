package ru.maksirep.core.service;

import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.maksirep.api.dto.*;
import ru.maksirep.api.dto.enums.UsersTypeDto;
import ru.maksirep.core.entity.Task;
import ru.maksirep.core.enums.Priority;
import ru.maksirep.core.enums.Status;
import ru.maksirep.core.error.ErrorCode;
import ru.maksirep.core.error.ServiceException;
import ru.maksirep.core.mapper.TaskDtoMapper;
import ru.maksirep.core.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CommentService commentService;
    private final UsersService usersService;
    private final TaskDtoMapper taskDtoMapper;

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_NUMBER = 0;

    public TaskService(TaskRepository taskRepository,
                       CommentService commentService,
                       UsersService usersService,
                       TaskDtoMapper taskDtoMapper) {
        this.taskRepository = taskRepository;
        this.commentService = commentService;
        this.usersService = usersService;
        this.taskDtoMapper = taskDtoMapper;
    }

    public void createTask(CreateTaskRequest createTaskRequest) {
        if (!usersService.isUserExistsById(createTaskRequest.executorId())) {
            throw new ServiceException(
                    String.format("Пользователь с идентификатором \"%d\" не найден", createTaskRequest.executorId()),
                    ErrorCode.NOT_FOUND);
        }
        taskRepository.createTask(
                createTaskRequest.header(),
                createTaskRequest.description(),
                createTaskRequest.status().name(),
                createTaskRequest.priority().name(),
                (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
                createTaskRequest.executorId()
        );
    }

    public TaskDto getTaskById(int id) {
        return taskDtoMapper.map(taskRepository.getTaskById(id).orElseThrow(() ->
                new ServiceException(String.format("Задача с идентификатором \"%d\" не найдена", id), ErrorCode.NOT_FOUND)));
    }

    public List<TaskDto> getTaskPagination(Integer page,
                                           Integer size,
                                           String header,
                                           String description,
                                           Status status,
                                           Priority priority,
                                           Integer authorId,
                                           Integer executorId) {
        return taskDtoMapper.map(
                taskRepository.getTaskPagination(
                        header,
                        description,
                        status == null ? null : status.name(),
                        priority == null ? null : priority.name(),
                        authorId,
                        executorId,
                        (size == null || size <= 0) ? DEFAULT_PAGE_SIZE : size,
                        (page == null || page < 0)? DEFAULT_PAGE_NUMBER : page
                ));
    }

    public void updateTaskById(int id, UpdateTaskRequest updateTaskRequest) {
        Task task = taskRepository.getTaskById(id).orElseThrow(() ->
                new ServiceException(String.format("Задача с идентификатором \"%d\" не найдена", id), ErrorCode.NOT_FOUND));
        int currentUser = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (task.getAuthorId() == currentUser) {
            if (updateTaskRequest.executorId() != null && (!usersService.isUserExistsById(updateTaskRequest.executorId()))) {
                throw new ServiceException(
                        String.format("Пользователь с идентификатором \"%d\" не найден", updateTaskRequest.executorId()),
                        ErrorCode.NOT_FOUND);
            }
            taskRepository.updateTaskByOwner(
                    id,
                    updateTaskRequest.header(),
                    updateTaskRequest.description(),
                    updateTaskRequest.status() != null ? updateTaskRequest.status().name() : null,
                    updateTaskRequest.priority() != null ? updateTaskRequest.priority().name() : null,
                    updateTaskRequest.executorId()
            );
        } else if (task.getExecutorId() == currentUser) {
            taskRepository.updateTaskByExecutor(
                    id,
                    updateTaskRequest.status() != null ? updateTaskRequest.status().name() : null
            );
        } else {
            throw new ServiceException("Недостаточно прав для редактирования задачи", ErrorCode.FORBIDDEN);
        }
    }

    @Transactional
    public void deleteTask(int id) {
        int currentUser = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Task> task = taskRepository.getTaskById(id);
        if (task.isPresent()) {
            if (task.get().getAuthorId() == currentUser) {
                if (!commentService.isCommentExistByTaskId(id)) {
                    commentService.deleteCommentsByTaskId(id);
                }
                taskRepository.deleteTaskById(id);
            } else {
                throw new ServiceException("Недостаточно прав для удаления задачи", ErrorCode.FORBIDDEN);
            }
        } else {
            throw new ServiceException(String.format("Задача с идентификатором \"%d\" не найдена", id),
                    ErrorCode.NOT_FOUND);
        }
    }

    public void createComment(CreateCommentRequest createCommentRequest) {
        if (!taskRepository.isTaskExistById(createCommentRequest.taskId())) {
            throw new ServiceException(
                    String.format("Задача с идентификатором \"%d\" не найдена", createCommentRequest.taskId()),
                    ErrorCode.NOT_FOUND);
        }
        commentService.createComment(createCommentRequest);
    }

    public List<TaskDto> getTasksByUser(int userId,
                                        UsersTypeDto usersTypeDto,
                                        Integer page,
                                        Integer size,
                                        String header,
                                        String description,
                                        Status status,
                                        Priority priority) {
        if (!usersService.isUserExistsById(userId)) {
            throw new ServiceException(
                    String.format("Пользователь с идентификатором \"%d\" не найден", userId),
                    ErrorCode.NOT_FOUND);
        }
        List<Task> resultTasks = new ArrayList<>();
        switch (usersTypeDto) {
            case AUTHOR -> resultTasks = taskRepository.getTaskPagination(
                    header,
                    description,
                    status == null ? null : status.name(),
                    priority == null ? null : priority.name(),
                    userId,
                    null,
                    (size == null || size <= 0) ? DEFAULT_PAGE_SIZE : size,
                    (page == null || page < 0)? DEFAULT_PAGE_NUMBER : page);
            case EXECUTOR -> resultTasks = taskRepository.getTaskPagination(
                    header,
                    description,
                    status == null ? null : status.name(),
                    priority == null ? null : priority.name(),
                    null,
                    userId,
                    (size == null || size <= 0) ? DEFAULT_PAGE_SIZE : size,
                    (page == null || page < 0)? DEFAULT_PAGE_NUMBER : page);
        }
        return taskDtoMapper.map(resultTasks);
    }

    public List<CommentDto> getTaskComments(int taskId,
                                            Integer commentAuthorId,
                                            String text,
                                            Integer page,
                                            Integer size) {
        if (!taskRepository.isTaskExistById(taskId)) {
            throw new ServiceException(
                    String.format("Задача с идентификатором \"%d\" не найдена", taskId),
                    ErrorCode.NOT_FOUND);
        }
        return commentService.getTaskComments(taskId, commentAuthorId, text, page, size);
    }
}
