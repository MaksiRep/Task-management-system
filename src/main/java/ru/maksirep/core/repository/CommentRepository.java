package ru.maksirep.core.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.maksirep.core.entity.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO comment (id, task_id, comment_author_id, text)
            VALUES (nextval('comment_id_seq'), :taskId, :commentAuthorId, :text)
            """, nativeQuery = true)
    void createComment(@Param("taskId") int id,
                       @Param("commentAuthorId") int commentAuthorId,
                       @Param("text") String text);

    @Query(value = """
            SELECT EXISTS(SELECT TRUE FROM comment WHERE task_id = :id)
            """, nativeQuery = true)
    boolean isCommentExistByTaskId(@Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = """
            DELETE
            FROM comment
            WHERE task_id = :id
            """, nativeQuery = true)
    void deleteCommentsByTaskId(@Param("id") int id);

    @Query(value = """
            SELECT *
            FROM comment
            WHERE (:taskId IS NULL OR comment.task_id = :taskId)
              AND (:text IS NULL OR comment.text LIKE '%' || :text || '%')
              AND (:commentAuthorId IS NULL OR comment.comment_author_id = :commentAuthorId)
            ORDER BY id
            LIMIT :size OFFSET :page * :size
            """, nativeQuery = true)
    List<Comment> getTaskComments(@Param("taskId") int taskId,
                                  @Param("commentAuthorId") Integer commentAuthorId,
                                  @Param("text") String text,
                                  @Param("size") Integer size,
                                  @Param("page") Integer page);

    @Query(value = """
            SELECT *
            FROM comment
            WHERE id = :id
             """, nativeQuery = true)
    Optional<Comment> getCommentById(@Param("id") int id);
}
