package io.ylab.wordflow.cli.service.impl;

import io.ylab.wordflow.cli.service.ITextAnalysis;
import io.ylab.wordflow.core.service.IFileAnalysisService;
import io.ylab.wordflow.core.service.impl.FileAnalysisServiceImpl;
import io.ylab.wordflow.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Реализация {@link ITextAnalysis} для анализа текстов в CLI-режиме.
 * Использует общий сервис {@link IFileAnalysisService} для выполнения бизнес-логики
 * и преобразует результат в {@link ResponseDto}.
 *
 * <p>Класс предназначен для консольного запуска приложения (профиль {@code cli}).
 * В отличие от REST-сервиса, анализ выполняется синхронно, и результат возвращается
 * только после завершения обработки всех файлов.</p>
 *
 * @see ITextAnalysis
 * @see IFileAnalysisService
 */
@Slf4j
@Service
@Profile("cli")
@RequiredArgsConstructor
public class TextAnalyzeServiceImpl implements ITextAnalysis {

    private final FileAnalysisServiceImpl fileAnalysisServiceImpl;

    /**
     * {@inheritDoc}
     *
     * <p>В данной реализации:
     * <ol>
     *   <li>Замер времени начала выполнения.</li>
     *   <li>Вызов {@link IFileAnalysisService#performAnalysis(RequestDto)}.</li>
     *   <li>Вычисление длительности выполнения.</li>
     *   <li>Формирование {@link InfoDto} с параметрами и статистикой.</li>
     *   <li>Преобразование {@code List<WordCount>} в {@code List<WordCountDto>}.</li>
     *   <li>Сборка и возврат {@link ResponseDto}.</li>
     * </ol>
     * </p>
     *
     * <p><b>Примечание:</b> Метод не выбрасывает исключений – все ошибки (включая неверную директорию)
     * аккумулируются внутри {@code ResponseDto.errors}.</p>
     *
     * @param request параметры анализа (не может быть {@code null})
     * @return {@link ResponseDto} с результатами анализа (может содержать ошибки)
     */
    @Override
    public ResponseDto analyze(RequestDto request) {
        long startTime = System.currentTimeMillis();

        AnalysisResult result = fileAnalysisServiceImpl.performAnalysis(request);

        long executionTime = System.currentTimeMillis() - startTime;

        InfoDto info = new InfoDto(
                request.directory(),
                request.minLength(),
                request.top(),
                request.mode().name().toLowerCase(),
                request.threads(),
                result.processedFiles(),
                executionTime
        );

        return new ResponseDto(info, result.wordsCount(), result.errors());
    }
}
