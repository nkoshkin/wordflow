package io.ylab.wordflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность для аудита действий пользователей.
 *
 * <p>Поля:
 * <ul>
 *   <li>{@code id} – первичный ключ</li>
 *   <li>{@code username} – имя пользователя (из SecurityContext)</li>
 *   <li>{@code timestamp} – время действия</li>
 *   <li>{@code action} – тип действия</li>
 *   <li>{@code parameters} – строковое представление параметров запроса</li>
 *   <li>{@code analysisId} – UUID анализа</li>
 * </ul>
 */
@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private LocalDateTime timestamp;
    private String action;
    private String parameters;
    private UUID analysisId;
}