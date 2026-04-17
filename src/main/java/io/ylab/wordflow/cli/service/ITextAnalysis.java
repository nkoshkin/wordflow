package io.ylab.wordflow.cli.service;

import io.ylab.wordflow.cli.service.impl.TextAnalyzeServiceImpl;
import io.ylab.wordflow.core.service.IFileAnalysisService;
import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.dto.ResponseDto;

/**
 * Интерфейс для синхронного анализа текстов (используется в CLI-режиме).
 * Предоставляет метод {@link #analyze(RequestDto)} для выполнения полного анализа
 * и возврата результата в формате {@link ResponseDto}.
 *
 * <p>Данный интерфейс предназначен для консольного запуска.</p>
 *
 * <p>Реализация ({@link TextAnalyzeServiceImpl}) использует общий сервис
 * {@link IFileAnalysisService} для выполнения бизнес-логики и преобразует результат
 * в DTO.</p>
 *
 * @see TextAnalyzeServiceImpl
 * @see IFileAnalysisService
 */
public interface ITextAnalysis {

    /**
     * Выполняет анализ текстов в указанной директории.
     *
     * @param requestDto параметры анализа (директория, minLength, top, стоп-слова, режим, потоки)
     * @return {@link ResponseDto} результат с метаинформацией, топ-словами и ошибками
     * @throws IllegalArgumentException если директория не существует или недоступна
     * @throws RuntimeException         если в процессе анализа произошла фатальная ошибка
     */
    ResponseDto analyze(RequestDto requestDto);

}
