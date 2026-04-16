package io.ylab.wordflow.service;

import io.ylab.wordflow.entity.User;
import io.ylab.wordflow.enums.Role;
import io.ylab.wordflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для управления пользователями.
 * Реализует {@link UserDetailsService} для загрузки данных пользователя по имени.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Загружает пользователя по имени для аутентификации.
     *
     * @param username имя пользователя
     * @return объект {@link UserDetails}, содержащий данные для аутентификации
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * Регистрирует нового пользователя с заданным именем, паролем и ролью.
     *
     * @param username имя пользователя
     * @param rawPassword пароль в открытом виде (будет закодирован)
     * @param role роль пользователя (USER или ADMIN)
     * @throws RuntimeException если пользователь с таким именем уже существует
     */
    @Transactional
    public void register(String username, String rawPassword, Role role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User already exists: " + username);
        }
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .build();
        userRepository.save(user);
        log.info("User '{}' registered with role {}", username, role);
    }

    /**
     * Проверяет существование пользователя по имени.
     *
     * @param username имя пользователя
     * @return true, если пользователь существует
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}