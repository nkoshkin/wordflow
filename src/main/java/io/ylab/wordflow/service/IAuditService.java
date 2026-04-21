package io.ylab.wordflow.service;

import io.ylab.wordflow.entity.AuditLog;
import io.ylab.wordflow.service.impl.AuditServiceImpl;

import java.util.UUID;

/**
 * Сервис аудита действий пользователей.
 * Предоставляет методы для логирования событий (запуск анализа, просмотр результатов и т.д.).
 *
 * <p>Все действия сохраняются в таблицу {@code audit_log} с указанием пользователя,
 * времени, типа действия и параметров.</p>
 *
 * @see AuditLog
 * @see AuditServiceImpl
 */
public interface IAuditService {

    /**
     * Сохраняет запись аудита для указанного действия.
     *
     * <p>Примеры действий:
     * <ul>
     *   <li>{@code "START_ANALYSIS"} – запуск анализа</li>
     *   <li>{@code "GET_RESULT"} – получение результата анализа</li>
     *   <li>{@code "LIST_RESULTS"} – получение списка анализов</li>
     * </ul>
     * </p>
     *
     * @param action тип действия (не может быть {@code null})
     * @param params строковое представление параметров запроса
     * @param analysisId идентификатор анализа (может быть {@code null}, если действие не связано с конкретным анализом)
     */
    void log(String action, String params, UUID analysisId);
}
