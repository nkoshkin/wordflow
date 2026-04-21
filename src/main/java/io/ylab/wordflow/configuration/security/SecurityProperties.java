package io.ylab.wordflow.configuration.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Конфигурация учётных данных дефолтного пользователя.
 * Загружается из {@code application.yml} по префиксу {@code app.security}.
 *
 * <p>Пример конфигурации:
 * <pre>
 * app:
 *   security:
 *     username: admin
 *     password: admin
 * </pre>
 * </p>
 *
 * @param username имя пользователя по умолчанию
 * @param password пароль пользователя по умолчанию
 */
@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
        String username,
        String password
) {

}
