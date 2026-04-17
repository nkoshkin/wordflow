package io.ylab.wordflow.service;

import io.ylab.wordflow.service.impl.JwtServiceImpl;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Сервис для работы с JWT-токенами.
 * Предоставляет методы генерации, валидации и извлечения данных из токена.
 *
 * @see JwtServiceImpl
 */
public interface IJwtService {

    /**
     * Генерирует JWT-токен для аутентифицированного пользователя.
     *
     * <p>В токен включаются:
     * <ul>
     *   <li>{@code subject} – имя пользователя</li>
     *   <li>{@code roles} – список ролей (полномочий)</li>
     *   <li>{@code issuedAt} – время выдачи</li>
     *   <li>{@code expiration} – время истечения (задаётся в конфигурации)</li>
     * </ul>
     * </p>
     *
     * @param username    имя пользователя (не может быть {@code null})
     * @param authorities коллекция полномочий (ролей) пользователя
     * @return подписанный JWT-токен в виде строки
     * @throws IllegalArgumentException если username пуст или authorities null
     */
    String generateToken(String username, Collection<? extends GrantedAuthority> authorities);

    /**
     * Проверяет валидность JWT-токена.
     *
     * <p>Проверяются:
     * <ul>
     *   <li>корректность подписи</li>
     *   <li>срок действия</li>
     * </ul>
     * </p>
     *
     * @param token JWT-токен (строка)
     * @return {@code true}, если токен действителен; {@code false} в противном случае
     */
    boolean validateToken(String token);

    /**
     * Извлекает имя пользователя (subject) из JWT-токена.
     *
     * @param token JWT-токен
     * @return имя пользователя
     * @throws io.jsonwebtoken.JwtException если токен невалиден (неверная подпись, истёк и т.д.)
     */
    String getUsernameFromToken(String token);
}