package io.ylab.wordflow.controller;

import io.ylab.wordflow.service.IJwtService;
import io.ylab.wordflow.service.impl.JwtServiceImpl;
import io.ylab.wordflow.dto.auth.JwtResponse;
import io.ylab.wordflow.dto.auth.LoginRequest;
import io.ylab.wordflow.dto.auth.RegisterRequest;
import io.ylab.wordflow.enums.Role;
import io.ylab.wordflow.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Контроллер для аутентификации и регистрации пользователей.
 * Предоставляет эндпоинты для входа и регистрации новых пользователей.
 *
 * <p>Эндпоинт {@code /login} доступен всем без аутентификации.
 * Эндпоинт {@code /register} требует прав администратора (роль {@code ADMIN}).</p>
 *
 * @see AuthenticationManager
 * @see JwtServiceImpl
 * @see IUserService
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final IJwtService jwtService;
    private final IUserService userServiceImpl;

    /**
     * Аутентифицирует пользователя и выдаёт JWT-токен.
     *
     * <p>При успешной аутентификации возвращается токен</p>
     *
     * @param request логин и пароль пользователя
     * @return JWT-токен
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        String token = jwtService.generateToken(request.username(), authentication.getAuthorities());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    /**
     * Регистрирует нового пользователя.
     *
     * <p>Роль по умолчанию – {@code ROLE_USER}. Если передана роль, она должна быть
     * одной из {@code ROLE_USER} или {@code ROLE_ADMIN}.</p>
     *
     * @param request данные для регистрации (логин, пароль, опционально роль)
     * @return ответ с сообщением об успехе (HTTP 200) или ошибке (HTTP 400)
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            Role role = (request.role() != null && !request.role().isBlank())
                    ? Role.valueOf(request.role().toUpperCase())
                    : Role.ROLE_USER;
            userServiceImpl.register(request.username(), request.password(), role);
            return ResponseEntity.ok(Map.of("message", "User registered successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role. Use ROLE_USER or ROLE_ADMIN"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
