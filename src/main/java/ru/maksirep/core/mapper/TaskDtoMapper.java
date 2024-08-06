package ru.maksirep.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.maksirep.api.dto.TaskDto;
import ru.maksirep.core.entity.Task;
import ru.maksirep.core.enums.Priority;
import ru.maksirep.core.enums.Status;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskDtoMapper {

    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    @Mapping(source = "priority", target = "priority", qualifiedByName = "priorityToString")
    TaskDto map(Task task);

    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    @Mapping(source = "priority", target = "priority", qualifiedByName = "priorityToString")
    List<TaskDto> map(List<Task> tasks);

    @Named("statusToString")
    static String mapStatusToString(Status status) {
        return status != null ? status.getValue() : null;
    }

    @Named("priorityToString")
    static String mapPriorityToString(Priority priority) {
        return priority != null ? priority.getValue() : null;
    }
}
