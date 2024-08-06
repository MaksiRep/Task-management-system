package ru.maksirep.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.maksirep.core.entity.Users;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {

    @Query(value = """
            SELECT *
            FROM users
            WHERE id = :id
            """, nativeQuery = true)
    Optional<Users> getUserById (@Param("id") int id);

    @Query(value = """
            SELECT *
            FROM users
            WHERE email = :email AND password = :password
            """, nativeQuery = true)
    Optional<Users> getUserByEmailAndPassword (@Param("email") String email,
                                               @Param("password") String password);

    @Query(value = """
            SELECT EXISTS(SELECT TRUE FROM users WHERE id = :id)
            """, nativeQuery = true)
    boolean isUserExistsById (@Param("id") int id);
}
