package io.ylab.wordflow.service.impl;

import io.ylab.wordflow.entity.User;
import io.ylab.wordflow.enums.Role;
import io.ylab.wordflow.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Тесты для {@link UserServiceImpl}.
 * Проверяют загрузку пользователя по имени, регистрацию нового пользователя
 * и проверку существования пользователя.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    /**
     * Проверяет, что {@link UserServiceImpl#loadUserByUsername(String)} возвращает пользователя,
     * если он существует в репозитории.
     */
    @Test
    void loadUserByUsername_whenUserExists_shouldReturnUserDetails() {
        // given
        String username = "testuser";
        User user = User.builder()
                .id(UUID.randomUUID())
                .username(username)
                .password("encodedPass")
                .role(Role.ROLE_USER)
                .build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // when
        UserDetails result = userService.loadUserByUsername(username);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getPassword()).isEqualTo("encodedPass");
        assertThat(result.getAuthorities()).hasSize(1);
        assertThat(result.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
        verify(userRepository, times(1)).findByUsername(username);
    }

    /**
     * Проверяет, что {@link UserServiceImpl#loadUserByUsername(String)} выбрасывает
     * {@link UsernameNotFoundException}, если пользователь не найден.
     */
    @Test
    void loadUserByUsername_whenUserNotExists_shouldThrowUsernameNotFoundException() {
        String username = "missing";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: " + username);
        verify(userRepository, times(1)).findByUsername(username);
    }

    /**
     * Проверяет, что {@link UserServiceImpl#register(String, String, Role)} сохраняет нового пользователя
     * с закодированным паролем и переданной ролью.
     */
    @Test
    void register_whenUserNotExists_shouldSaveUserWithEncodedPassword() {
        String username = "newuser";
        String rawPassword = "password";
        Role role = Role.ROLE_ADMIN;
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn("encodedPassword");

        userService.register(username, rawPassword, role);

        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Проверяет, что при регистрации пользователя с уже существующим именем
     * выбрасывается {@link RuntimeException} и сохранение не происходит.
     */
    @Test
    void register_whenUserAlreadyExists_shouldThrowExceptionAndNotSave() {
        String username = "existing";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mock(User.class)));

        assertThatThrownBy(() -> userService.register(username, "pass", Role.ROLE_USER))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User already exists: " + username);
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Проверяет, что если роль не передана (null), по умолчанию устанавливается {@link Role#ROLE_USER}.
     */
    @Test
    void register_whenRoleIsNull_shouldSetDefaultRoleUser() {
        String username = "newuser";
        String rawPassword = "password";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn("encodedPassword");

        userService.register(username, rawPassword, null);

        verify(userRepository, times(1)).save(argThat(user ->
                user.getRole() == Role.ROLE_USER
        ));
    }

    /**
     * Проверяет, что {@link UserServiceImpl#existsByUsername(String)} возвращает {@code true},
     * если пользователь существует.
     */
    @Test
    void existsByUsername_whenUserExists_shouldReturnTrue() {
        String username = "existing";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mock(User.class)));

        boolean exists = userService.existsByUsername(username);

        assertThat(exists).isTrue();
        verify(userRepository, times(1)).findByUsername(username);
    }

    /**
     * Проверяет, что {@link UserServiceImpl#existsByUsername(String)} возвращает {@code false},
     * если пользователь не существует.
     */
    @Test
    void existsByUsername_whenUserNotExists_shouldReturnFalse() {
        String username = "missing";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        boolean exists = userService.existsByUsername(username);

        assertThat(exists).isFalse();
        verify(userRepository, times(1)).findByUsername(username);
    }
}