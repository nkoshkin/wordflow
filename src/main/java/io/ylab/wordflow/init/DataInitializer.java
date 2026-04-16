package io.ylab.wordflow.init;

import io.ylab.wordflow.enums.Role;
import io.ylab.wordflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Инициализатор базы данных.
 * Cоздаёт дефолтного пользователя, если он ещё не существует. Параметры берутся из application.yml.
 */

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    @Value("${app.security.username}")
    private String defaultUsername;

    @Value("${app.security.password}")
    private String defaultPassword;

    @Value("${app.security.role}")
    private String defaultRole;

    @Override
    public void run(String... args) {
        if (!userService.existsByUsername(defaultUsername)) {
            userService.register(defaultUsername, defaultPassword, Role.valueOf(defaultRole.toUpperCase()));
            System.out.println("Default user created: " + defaultUsername);
        }
    }
}