package ru.maksirep.core.mapper;

import org.mapstruct.Mapper;
import ru.maksirep.api.dto.CommentDto;
import ru.maksirep.core.entity.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentDtoMapper {

    List<CommentDto> map(List<Comment> comment);
}
