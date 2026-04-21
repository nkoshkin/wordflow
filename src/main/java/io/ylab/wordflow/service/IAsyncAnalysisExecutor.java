package io.ylab.wordflow.service;

import io.ylab.wordflow.controller.AnalysisController;
import io.ylab.wordflow.dto.AnalysisResult;
import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.service.impl.AsyncAnalysisExecutorImpl;

import java.util.UUID;

/**
 * Асинхронный исполнитель анализа текстов.
 * Запускает анализ в отдельном потоке и сохраняет результат в базу данных.
 *
 * <p>Используется в {@link AnalysisController} для неблокирующего запуска длительных операций.
 * Реализация должна быть помечена {@code @Async}.</p>
 *
 * @see AsyncAnalysisExecutorImpl
 */
public interface IAsyncAnalysisExecutor {

    /**
     * Асинхронно выполняет анализ текстов в указанной директории.
     *
     * <p>Метод не возвращает результат напрямую. После завершения анализа результат сохраняется
     * через {@link IAnalysisService#saveAnalysisResult(UUID, AnalysisResult)} .</p>
     *
     * <p>Статус анализа в БД обновляется на {@code RUNNING} перед началом обработки
     * и на {@code COMPLETED} или {@code FAILED} после завершения.</p>
     *
     * @param analysisId уникальный идентификатор анализа (должен существовать в БД)
     * @param request параметры анализа (директория, длина слова, режим и т.д.)
     */
    void execute(UUID analysisId, RequestDto request);
}
