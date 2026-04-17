package io.ylab.wordflow.service.impl;

import io.ylab.wordflow.core.service.impl.FileAnalysisServiceImpl;
import io.ylab.wordflow.dto.AnalysisResult;
import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.service.IAnalysisService;
import io.ylab.wordflow.service.IAsyncAnalysisExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Реализация асинхронного исполнителя анализа текстов.
 * Запускает анализ и сохраняет результат в БД.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncAnalysisExecutorImpl implements IAsyncAnalysisExecutor {

    private final FileAnalysisServiceImpl fileAnalysisServiceImpl;
    private final IAnalysisService analysisService;

    /**
     * {@inheritDoc}
     *
     * <p>Метод помечен {@code @Async}, поэтому выполняется в пуле потоков.
     * Сначала вызывается анализ, затем результат сохраняется в БД.
     * Ошибки логируются, но не прерывают выполнение других анализов.</p>
     *
     * @param analysisId идентификатор анализа
     * @param request параметры анализа
     */
    @Override
    @Async
    public void execute(UUID analysisId, RequestDto request) {
        AnalysisResult result = fileAnalysisServiceImpl.performAnalysis(request);
        analysisService.saveAnalysisResult(analysisId, result);
        log.info("Analysis {} completed", analysisId);
    }

}
