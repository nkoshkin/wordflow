package io.ylab.wordflow.service.impl;

import io.ylab.wordflow.entity.AuditLog;
import io.ylab.wordflow.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Реализация сервиса аудита.
 * Сохраняет записи о действиях пользователей в таблицу {@code audit_log}.
 *
 * <p>Извлекает имя текущего пользователя из {@link SecurityContextHolder}.</p>
 */
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements io.ylab.wordflow.service.IAuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * {@inheritDoc}
     *
     * <p>Создаёт запись аудита с текущим пользователем, временной меткой,
     * действием, параметрами и идентификатором анализа (если передан).</p>
     *
     * @param action     тип действия
     * @param parameters параметры запроса
     * @param analysisId идентификатор анализа (может быть {@code null})
     */
    @Override
    public void log(String action, String parameters, UUID analysisId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        AuditLog log = AuditLog.builder()
                .username(username)
                .timestamp(LocalDateTime.now())
                .action(action)
                .parameters(parameters)
                .analysisId(analysisId)
                .build();
        auditLogRepository.save(log);
    }
}
