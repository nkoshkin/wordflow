package io.ylab.wordflow.repository;

import io.ylab.wordflow.entity.ErrorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link ErrorEntity}.
 * Предоставляет методы для получения ошибок по идентификатору анализа.
 */
@Repository
public interface ErrorRepository extends JpaRepository<ErrorEntity, Long> {

    /**
     * Находит все ошибки, связанные с конкретным анализом.
     * @param analysisId идентификатор анализа
     * @return список сущностей {@link ErrorEntity} для указанного анализа,
     *         может быть пустым, если ошибок не было
     */
    List<ErrorEntity> findByAnalysisId(UUID analysisId);
}
