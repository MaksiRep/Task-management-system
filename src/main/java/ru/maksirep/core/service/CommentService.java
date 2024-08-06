package ru.maksirep.core.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.maksirep.api.dto.CommentDto;
import ru.maksirep.api.dto.CreateCommentRequest;
import ru.maksirep.core.mapper.CommentDtoMapper;
import ru.maksirep.core.repository.CommentRepository;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentDtoMapper commentDtoMapper;

    public CommentService(CommentRepository commentRepository, CommentDtoMapper commentDtoMapper) {
        this.commentRepository = commentRepository;
        this.commentDtoMapper = commentDtoMapper;
    }

    public void createComment(CreateCommentRequest createCommentRequest) {
        commentRepository.createComment(
                createCommentRequest.taskId(),
                (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
                createCommentRequest.text());
    }

    public boolean isCommentExistByTaskId(int taskId) {
        return commentRepository.isCommentExistByTaskId(taskId);
    }

    public void deleteCommentsByTaskId(int taskId) {
        commentRepository.deleteCommentsByTaskId(taskId);
    }

    public List<CommentDto> getTaskComments(int taskId,
                                            Integer commentAuthorId,
                                            String text,
                                            Integer page,
                                            Integer size) {
        return commentDtoMapper.map(
                commentRepository.getTaskComments(taskId, commentAuthorId, text, page, size)
        );
    }
}