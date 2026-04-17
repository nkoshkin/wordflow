package io.ylab.wordflow.service.impl;

import io.ylab.wordflow.entity.User;
import io.ylab.wordflow.enums.Role;
import io.ylab.wordflow.repository.UserRepository;
import io.ylab.wordflow.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация сервиса для работы с пользователями.
 * Обеспечивает загрузку пользователя по имени, регистрацию и проверку существования.
 *
 * <p>Используется Spring Security для аутентификации.</p>
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * {@inheritDoc}
     *
     * <p>Загружает пользователя из базы данных. Если пользователь не найден,
     * выбрасывает {@link UsernameNotFoundException}.</p>
     *
     * @param username имя пользователя
     * @return {@link UserDetails} пользователя
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Регистрирует нового пользователя с кодированием пароля.
     * Если пользователь с таким именем уже существует, выбрасывает {@link RuntimeException}.</p>
     *
     * @param username имя пользователя
     * @param rawPassword пароль в открытом виде
     * @param role роль пользователя
     * @throws RuntimeException если пользователь уже существует
     */
    @Override
    @Transactional
    public void register(String username, String rawPassword, Role role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User already exists: " + username);
        }
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .role(role != null ? role : Role.ROLE_USER)
                .build();
        userRepository.save(user);
    }

    /**
     * {@inheritDoc}
     *
     * @param username имя пользователя
     * @return true, если пользователь существует
     */
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}