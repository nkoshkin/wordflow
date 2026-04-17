package io.ylab.wordflow.dto.auth;

/**
 * DTO для запроса аутентификации.
 *
 * @param username имя пользователя (логин)
 * @param password пароль пользователя
 */
public record LoginRequest(String username, String password) {}
