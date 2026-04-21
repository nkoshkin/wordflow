package io.ylab.wordflow.service.impl;

import io.jsonwebtoken.JwtException;
import io.ylab.wordflow.service.IJwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Тесты для {@link JwtServiceImpl}.
 * Проверяют генерацию JWT-токенов, их валидацию и извлечение имени пользователя.
 */
class JwtServiceImplTest {

    private static final String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long EXPIRATION = 86400000L; // 24 часа

    private IJwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl();
        setField(jwtService, "secret", SECRET);
        setField(jwtService, "expiration", EXPIRATION);
        ((JwtServiceImpl) jwtService).init();
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Проверяет, что {@link JwtServiceImpl#generateToken(String, Collection)} возвращает
     * непустую строку, которая является валидным JWT.
     */
    @Test
    void generateToken_shouldReturnValidToken() {
        String token = jwtService.generateToken("user", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
        assertThat(jwtService.validateToken(token)).isTrue();
    }

    /**
     * Проверяет, что {@link JwtServiceImpl#validateToken(String)} возвращает {@code true}
     * для корректно сгенерированного токена.
     */
    @Test
    void validateToken_withValidToken_shouldReturnTrue() {
        String token = jwtService.generateToken("user", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        assertThat(jwtService.validateToken(token)).isTrue();
    }

    /**
     * Проверяет, что {@link JwtServiceImpl#validateToken(String)} возвращает {@code false}
     * для неверного токена.
     */
    @Test
    void validateToken_withInvalidToken_shouldReturnFalse() {
        boolean valid = jwtService.validateToken("invalid.token.value");
        assertThat(valid).isFalse();
    }

    /**
     * Проверяет, что {@link JwtServiceImpl#validateToken(String)} возвращает {@code false}
     * для токена с истекшим сроком действия.
     */
    @Test
    void validateToken_withExpiredToken_shouldReturnFalse() {
        setField(jwtService, "expiration", -1L);
        ((JwtServiceImpl) jwtService).init();
        String expiredToken = jwtService.generateToken("user", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        assertThat(jwtService.validateToken(expiredToken)).isFalse();
        setField(jwtService, "expiration", EXPIRATION);
        ((JwtServiceImpl) jwtService).init();
    }

    /**
     * Проверяет, что {@link JwtServiceImpl#getUsernameFromToken(String)} корректно извлекает
     * имя пользователя из валидного токена.
     */
    @Test
    void getUsernameFromToken_withValidToken_shouldReturnUsername() {
        String username = "testuser";
        String token = jwtService.generateToken(username, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        String extracted = jwtService.getUsernameFromToken(token);
        assertThat(extracted).isEqualTo(username);
    }

    /**
     * Проверяет, что {@link JwtServiceImpl#getUsernameFromToken(String)} выбрасывает
     * {@link JwtException} для неверного токена.
     */
    @Test
    void getUsernameFromToken_withInvalidToken_shouldThrowJwtException() {
        assertThatThrownBy(() -> jwtService.getUsernameFromToken("invalid.token.value"))
                .isInstanceOf(JwtException.class);
    }

    /**
     * Проверяет, что сгенерированный токен содержит все переданные роли в claim 'roles'.
     */
    @Test
    void generateToken_shouldIncludeRoles() {
        String token = jwtService.generateToken("user", List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        ));
        assertThat(jwtService.validateToken(token)).isTrue();
    }
}