package io.ylab.wordflow.repository;

import io.ylab.wordflow.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link AuditLog}.
 * Предоставляет методы для получения записей аудита.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Находит записи аудита, связанные с конкретным анализом.
     *
     * @param analysisId идентификатор анализа (UUID)
     * @return список записей аудита для указанного анализа
     */
    List<AuditLog> findByAnalysisId(UUID analysisId);
}
