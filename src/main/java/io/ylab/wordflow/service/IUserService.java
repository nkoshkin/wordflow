package io.ylab.wordflow.service;

import io.ylab.wordflow.enums.Role;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Сервис для работы с пользователями.
 * Предоставляет методы для регистрации, проверки существования и загрузки пользователя по имени.
 *
 * <p>Реализует {@link UserDetailsService} для интеграции со Spring Security.</p>
 */
public interface IUserService extends UserDetailsService {

    /**
     * Регистрирует нового пользователя с заданной ролью.
     *
     * @param username    имя пользователя (логин)
     * @param rawPassword пароль в открытом виде (будет закодирован)
     * @param role        роль пользователя (ROLE_USER или ROLE_ADMIN)
     * @throws RuntimeException если пользователь с таким именем уже существует
     */
    void register(String username, String rawPassword, Role role);

    /**
     * Проверяет существование пользователя по имени.
     *
     * @param username имя пользователя
     * @return true, если пользователь существует, иначе false
     */
    boolean existsByUsername(String username);
}
