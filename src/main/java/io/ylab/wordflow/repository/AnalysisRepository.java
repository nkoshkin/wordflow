package io.ylab.wordflow.repository;

import io.ylab.wordflow.entity.AnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link AnalysisEntity}.
 * Предоставляет методы для получения анализов с сортировкой по времени.
 */
@Repository
public interface AnalysisRepository extends JpaRepository<AnalysisEntity, UUID> {

    /**
     * Возвращает все анализы, отсортированные по времени начала от новых к старым.
     * @return список сущностей {@link AnalysisEntity}, отсортированный по {@code startTime} DESC
     */
    List<AnalysisEntity> findAllByOrderByStartTimeDesc();
}
