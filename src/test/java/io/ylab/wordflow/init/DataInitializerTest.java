package io.ylab.wordflow.init;

import io.ylab.wordflow.enums.Role;
import io.ylab.wordflow.service.IUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

/**
 * Тесты для {@link DataInitializer}.
 * Проверяют создание дефолтного пользователя при старте приложения.
 */
@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private IUserService userService;

    @InjectMocks
    private DataInitializer dataInitializer;

    /**
     * Проверяет, что при отсутствии дефолтного пользователя в базе,
     * {@link DataInitializer#run(String...)} вызывает метод регистрации.
     */
    @Test
    void run_whenDefaultUserNotExists_shouldRegisterUser() {
        ReflectionTestUtils.setField(dataInitializer, "defaultUsername", "admin");
        ReflectionTestUtils.setField(dataInitializer, "defaultPassword", "admin");
        ReflectionTestUtils.setField(dataInitializer, "defaultRole", "ROLE_ADMIN");
        when(userService.existsByUsername("admin")).thenReturn(false);

        dataInitializer.run();

        verify(userService, times(1)).register("admin", "admin", Role.ROLE_ADMIN);
    }

    /**
     * Проверяет, что если дефолтный пользователь уже существует,
     * регистрация не вызывается.
     */
    @Test
    void run_whenDefaultUserExists_shouldNotRegisterUser() {
        ReflectionTestUtils.setField(dataInitializer, "defaultUsername", "admin");
        when(userService.existsByUsername("admin")).thenReturn(true);

        dataInitializer.run();

        verify(userService, never()).register(any(), any(), any());
    }
}