package ru.maksirep.core.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import ru.maksirep.core.enums.Priority;
import ru.maksirep.core.enums.Status;

import java.util.Objects;

@Entity
public class Task {

    @Id
    private Integer id;
    private String header;
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private Priority priority;
    private int authorId;
    private int executorId;

    public Task() {
    }

    public Task(Integer id,
                String header,
                String description,
                Status status,
                Priority priority,
                int authorId,
                int executorId) {
        this.id = id;
        this.header = header;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.authorId = authorId;
        this.executorId = executorId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getExecutorId() {
        return executorId;
    }

    public void setExecutorId(int executorId) {
        this.executorId = executorId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, header, description, status, priority, authorId, executorId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Task task = (Task) obj;
        return Objects.equals(id, task.id) &&
                Objects.equals(header, task.header) &&
                Objects.equals(description, task.description) &&
                status == task.status &&
                priority == task.priority &&
                authorId == task.authorId &&
                executorId == task.executorId;
    }
}
