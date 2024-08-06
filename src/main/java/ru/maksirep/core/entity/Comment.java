package ru.maksirep.core.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Comment {

    @Id
    private Integer id;
    private int taskId;
    private int commentAuthorId;
    private String text;

    public Comment() {
    }

    public Comment(Integer id, int taskId, int commentAuthorId, String text) {
        this.id = id;
        this.taskId = taskId;
        this.commentAuthorId = commentAuthorId;
        this.text = text;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getCommentAuthorId() {
        return commentAuthorId;
    }

    public void setCommentAuthorId(int commentAuthorId) {
        this.commentAuthorId = commentAuthorId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskId, commentAuthorId, text);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Comment comment = (Comment) obj;
        return Objects.equals(id, comment.id) &&
                Objects.equals(text, comment.text) &&
                taskId == comment.taskId &&
                commentAuthorId == comment.commentAuthorId;
    }
}
