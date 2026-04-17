package io.ylab.wordflow.repository;

import io.ylab.wordflow.entity.WordCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link WordCountEntity}.
 * Предоставляет методы для получения слов с частотами по идентификатору анализа.
 */
@Repository
public interface WordCountRepository extends JpaRepository<WordCountEntity, Long> {

    /**
     * Находит все слова (с частотами), связанные с конкретным анализом.
     * @param analysisId идентификатор анализа (UUID)
     * @return список сущностей {@link WordCountEntity} для указанного анализа
     */
    List<WordCountEntity> findByAnalysisId(UUID analysisId);
}
