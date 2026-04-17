package io.ylab.wordflow.init;

import io.ylab.wordflow.enums.Role;
import io.ylab.wordflow.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Инициализатор базы данных.
 * Cоздаёт дефолтного пользователя, если он ещё не существует. Параметры берутся из application.yml.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserServiceImpl userServiceImpl;

    @Value("${app.security.username}")
    private String defaultUsername;

    @Value("${app.security.password}")
    private String defaultPassword;

    @Value("${app.security.role}")
    private String defaultRole;

    @Override
    public void run(String... args) {
        if (!userServiceImpl.existsByUsername(defaultUsername)) {
            userServiceImpl.register(defaultUsername, defaultPassword, Role.valueOf(defaultRole.toUpperCase()));
            log.info("Default user created: {}", defaultUsername);
        }
    }
}