package io.ylab.wordflow.repository;

import io.ylab.wordflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link User}.
 * Предоставляет методы для поиска пользователей по имени.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Находит пользователя по уникальному имени (логину).
     *
     * @param username имя пользователя
     * @return {@link Optional}, содержащий найденного пользователя, или пустой {@code Optional},
     *         если пользователь с таким именем не существует
     */
    Optional<User> findByUsername(String username);
}
