package io.ylab.wordflow.dto.auth;

/**
 * DTO для запроса регистрации нового пользователя.
 *
 * @param username имя пользователя
 * @param password пароль пользователя
 * @param role     роль пользователя (ROLE_USER или ROLE_ADMIN)
 */
public record RegisterRequest(String username, String password, String role) {}
