package ru.maksirep.core.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.maksirep.core.entity.Task;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO task (id, header, description, status, priority, author_id, executor_id)
            VALUES (nextval('task_id_seq'), :header, :description, :status, :priority, :authorId, :executorId)
            """, nativeQuery = true)
    void createTask(@Param("header") String header,
                    @Param("description") String description,
                    @Param("status") String status,
                    @Param("priority") String priority,
                    @Param("authorId") int authorId,
                    @Param("executorId") int executorId);

    @Query(value = """
            SELECT EXISTS(SELECT TRUE FROM task WHERE id = :id)
            """, nativeQuery = true)
    boolean isTaskExistById(@Param("id") int id);

    @Query(value = """
            SELECT *
            FROM task
            WHERE id = :id
            """, nativeQuery = true)
    Optional<Task> getTaskById(@Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE task
            SET header      = COALESCE(:header, header),
                description = COALESCE(:description, description),
                status      = COALESCE(:status, status),
                priority    = COALESCE(:priority, priority),
                executor_id = COALESCE(:executorId, executor_id)
            WHERE id = :id
                        """, nativeQuery = true)
    void updateTaskByOwner(@Param("id") int id,
                           @Param("header") String header,
                           @Param("description") String description,
                           @Param("status") String status,
                           @Param("priority") String priority,
                           @Param("executorId") Integer executorId);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE task
            SET status = COALESCE(:status, status)
            WHERE id = :id
            """, nativeQuery = true)
    void updateTaskByExecutor(@Param("id") int id,
                              @Param("status") String status);

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM task WHERE id = :id
            """, nativeQuery = true)
    void deleteTaskById(@Param("id") int id);

    @Query(value = """
            SELECT *
            FROM task
            WHERE (:header IS NULL OR task.header LIKE '%' || :header || '%')
              AND (:description IS NULL OR task.description LIKE '%' || :description || '%')
              AND (:status IS NULL OR task.status LIKE '%' || :status || '%')
              AND (:priority IS NULL OR task.priority LIKE '%' || :priority || '%')
              AND (:authorId IS NULL OR task.author_id = :authorId)
              AND (:executorId IS NULL OR task.executor_id = :executorId)
            ORDER BY id
            LIMIT :size OFFSET :page * :size
            """, nativeQuery = true)
    List<Task> getTaskPagination(@Param("header") String header,
                                 @Param("description") String description,
                                 @Param("status") String status,
                                 @Param("priority") String priority,
                                 @Param("authorId") Integer authorId,
                                 @Param("executorId") Integer executorId,
                                 @Param("size") Integer size,
                                 @Param("page") Integer page);
}
