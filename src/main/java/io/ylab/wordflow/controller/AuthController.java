package io.ylab.wordflow.controller;

import io.ylab.wordflow.configuration.security.JwtService;
import io.ylab.wordflow.dto.auth.JwtResponse;
import io.ylab.wordflow.dto.auth.LoginRequest;
import io.ylab.wordflow.dto.auth.RegisterRequest;
import io.ylab.wordflow.enums.Role;
import io.ylab.wordflow.service.UserService;
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
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    /**
     * Аутентификация пользователя и выдача JWT-токена.
     *
     * @param request логин и пароль
     * @return JWT токен
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
     * Регистрация нового пользователя с ролью USER.
     *
     * @param request логин, пароль, роль (опционально)
     * @return статус 200 OK при успешной регистрации
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            Role role = (request.role() != null && !request.role().isBlank())
                    ? Role.valueOf(request.role().toUpperCase())
                    : Role.ROLE_USER;
            userService.register(request.username(), request.password(), role);
            return ResponseEntity.ok(Map.of("message", "User registered successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role. Use ROLE_USER or ROLE_ADMIN"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
