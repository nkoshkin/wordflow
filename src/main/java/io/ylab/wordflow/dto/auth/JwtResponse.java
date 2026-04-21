package io.ylab.wordflow.dto.auth;

/**
 * DTO для ответа с JWT-токеном.
 *
 * @param token JWT-токен
 */
public record JwtResponse(String token) {}
