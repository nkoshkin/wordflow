package io.ylab.wordflow.configuration.security;

import io.ylab.wordflow.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Основная конфигурация безопасности Spring Security.
 * Настраивает:
 * <ul>
 *   <li>отключение CSRF</li>
 *   <li>правила авторизации (публичные эндпоинты, доступ по ролям)</li>
 *   <li>управление сессиями (stateless)</li>
 *   <li>добавление JWT-фильтра перед стандартным фильтром аутентификации</li>
 * </ul>
 *
 * <p>Роли:
 * <ul>
 *   <li>{@code ADMIN}</li>
 *   <li>{@code USER}</li>
 * </ul>
 * </p>
 *
 * @see JwtAuthenticationFilter
 * @see UserServiceImpl
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserServiceImpl userServiceImpl;

    /**
     * Настраивает цепочку фильтров безопасности.
     *
     * @param http объект {@link HttpSecurity}
     * @return настроенный {@link SecurityFilterChain}
     * @throws Exception если конфигурация не удалась
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/register").hasRole("ADMIN")
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/analyze").hasRole("ADMIN")
                        .requestMatchers("/api/results/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .userDetailsService(userServiceImpl)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Предоставляет менеджер аутентификации, необходимый для эндпоинта /login.
     *
     * @param config конфигурация аутентификации
     * @return {@link AuthenticationManager}
     * @throws Exception если не удалось получить менеджер
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}