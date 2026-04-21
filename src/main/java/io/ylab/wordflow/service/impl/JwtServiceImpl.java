package io.ylab.wordflow.service.impl;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.ylab.wordflow.service.IJwtService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Сервис для работы с JWT-токенами.
 *
 * <p>Отвечает за:
 * <ul>
 *   <li>генерацию токена на основе имени пользователя и его ролей</li>
 *   <li>валидацию токена (подпись, срок действия)</li>
 *   <li>извлечение имени пользователя из токена</li>
 * </ul>
 * </p>
 *
 * <p>Секретный ключ и время жизни задаются в {@code application.yml}:
 * <pre>
 * jwt:
 *   secret: "..."
 *   expiration: 86400000
 * </pre>
 * </p>
 */
@Service
public class JwtServiceImpl implements IJwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey secretKey;

    /**
     * Инициализирует секретный ключ на основе строки из конфигурации.
     */
    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Реализация использует {@link Jwts#builder()} с установкой subject, claims,
     * issuedAt, expiration и подписью с помощью {@link #secretKey}.</p>
     *
     * @param username    имя пользователя
     * @param authorities полномочия
     * @return подписанный токен
     */
    @Override
    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
        return Jwts.builder()
                .subject(username)
                .claim("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Проверка производится через {@link Jwts#parser()}. При любом исключении (подпись, срок)
     * возвращается {@code false}.</p>
     *
     * @param token JWT-токен
     * @return true, если токен действителен
     */
    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Извлекает subject из токена. В случае невалидного токена выбрасывает {@link JwtException},
     * который перехватывается вызывающим кодом (обычно фильтром).</p>
     *
     * @param token JWT-токен
     * @return имя пользователя
     * @throws JwtException если токен невалиден
     */
    @Override
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}