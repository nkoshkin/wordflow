package io.ylab.wordflow.entity;

import io.ylab.wordflow.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Сущность пользователя, реализующая {@link UserDetails}.
 * Хранит учётные данные и роль для аутентификации и авторизации.
 *
 * <p>Поля:
 * <ul>
 *   <li>{@code id} – UUID, первичный ключ (генерируется автоматически)</li>
 *   <li>{@code username} – уникальное имя пользователя</li>
 *   <li>{@code password} – зашифрованный пароль</li>
 *   <li>{@code role} – роль (ROLE_USER или ROLE_ADMIN)</li>
 * </ul>
 *
 * <p>Все методы {@code UserDetails}, кроме {@code getAuthorities()},
 * возвращают {@code true} (учётные записи не блокируются, не истекают).
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return username;}

    @Override
    public boolean isAccountNonExpired() {return true;}

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
