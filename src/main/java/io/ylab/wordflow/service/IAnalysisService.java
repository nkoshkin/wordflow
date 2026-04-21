package io.ylab.wordflow.service;

import io.ylab.wordflow.dto.AnalysisResult;
import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.entity.AnalysisEntity;
import io.ylab.wordflow.service.impl.AnalysisServiceImpl;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * Сервис управления анализами.
 * Предоставляет синхронные операции для создания, получения и сохранения результатов анализов.
 *
 * <p>Все методы, изменяющие состояние, должны выполняться в транзакции.</p>
 *
 * @see AnalysisServiceImpl
 */
public interface IAnalysisService {

    /**
     * Создаёт новую запись анализа в статусе {@code PENDING} и запускает асинхронную обработку.
     *
     * <p>После сохранения записи вызывается {@link IAsyncAnalysisExecutor#execute(UUID, RequestDto)}.</p>
     *
     * @param request параметры анализа
     * @return уникальный идентификатор (UUID) созданного анализа
     */
    UUID startAnalysis(RequestDto request);

    /**
     * Возвращает сущность анализа по идентификатору.
     *
     * @param id идентификатор анализа
     * @return сущность {@link AnalysisEntity}
     * @throws ResponseStatusException с кодом 404, если анализ не найден
     */
    AnalysisEntity getAnalysis(UUID id);

    /**
     * Возвращает список всех анализов, отсортированных по времени начала (новые первыми).
     *
     * @return список сущностей {@link AnalysisEntity}
     */
    List<AnalysisEntity> getAllAnalysis();

    /**
     * Сохраняет результат завершённого анализа в базу данных.
     *
     * <p>Обновляет сущность анализа:
     * <ul>
     *   <li>устанавливает статус {@code COMPLETED} или {@code FAILED}</li>
     *   <li>сохраняет топ-слова и ошибки</li>
     *   <li>записывает количество обработанных файлов и время выполнения</li>
     * </ul>
     * </p>
     *
     * @param analysisId идентификатор анализа
     * @param result результат анализа (слова, ошибки, статистика)
     * @throws RuntimeException если анализ с указанным ID не найден
     */
    void saveAnalysisResult(UUID analysisId, AnalysisResult result);
}
