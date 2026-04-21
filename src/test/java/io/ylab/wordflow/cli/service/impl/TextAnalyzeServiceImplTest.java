package io.ylab.wordflow.cli.service.impl;

import io.ylab.wordflow.core.service.impl.FileAnalysisServiceImpl;
import io.ylab.wordflow.dto.*;
import io.ylab.wordflow.enums.ProcessingMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Тесты для {@link TextAnalyzeServiceImpl}.
 * Проверяют синхронный анализ текстов в CLI-режиме: вызов бизнес-логики,
 * измерение времени, формирование DTO.
 */
@ExtendWith(MockitoExtension.class)
class TextAnalyzeServiceImplTest {

    @Mock
    private FileAnalysisServiceImpl fileAnalysisServiceImpl;

    @InjectMocks
    private TextAnalyzeServiceImpl textAnalyzeService;

    /**
     * Проверяет, что {@link TextAnalyzeServiceImpl#analyze(RequestDto)}:
     * <ul>
     *   <li>вызывает {@link FileAnalysisServiceImpl#performAnalysis(RequestDto)}</li>
     *   <li>корректно вычисляет время выполнения</li>
     *   <li>преобразует результат в {@link ResponseDto} с правильными полями</li>
     * </ul>
     */
    @Test
    void analyze_shouldCallPerformAnalysisAndBuildResponse() {
        // given
        RequestDto request = new RequestDto("./dir", 3, 10, null, null, ProcessingMode.MULTI, 2);
        List<WordCountDto> wordCounts = List.of(
                new WordCountDto("word1", 10),
                new WordCountDto("word2", 5)
        );
        List<ErrorDto> errors = List.of(new ErrorDto("error.txt", "Access denied"));
        AnalysisResult mockResult = new AnalysisResult(wordCounts, errors, 2, 0L);
        when(fileAnalysisServiceImpl.performAnalysis(request)).thenReturn(mockResult);

        ResponseDto response = textAnalyzeService.analyze(request);

        verify(fileAnalysisServiceImpl, times(1)).performAnalysis(request);
        assertThat(response).isNotNull();

        InfoDto info = response.infoDto();
        assertThat(info.directory()).isEqualTo("./dir");
        assertThat(info.minLength()).isEqualTo(3);
        assertThat(info.top()).isEqualTo(10);
        assertThat(info.mode()).isEqualTo("multi");
        assertThat(info.threads()).isEqualTo(2);
        assertThat(info.processedFiles()).isEqualTo(2);
        assertThat(info.executionTimeMs()).isGreaterThanOrEqualTo(0);

        assertThat(response.words()).hasSize(2);
        assertThat(response.words().get(0).word()).isEqualTo("word1");
        assertThat(response.words().get(0).count()).isEqualTo(10);
        assertThat(response.words().get(1).word()).isEqualTo("word2");
        assertThat(response.words().get(1).count()).isEqualTo(5);

        assertThat(response.errors()).hasSize(1);
        assertThat(response.errors().getFirst().file()).isEqualTo("error.txt");
        assertThat(response.errors().getFirst().message()).isEqualTo("Access denied");
    }

    /**
     * Проверяет, что метод {@link TextAnalyzeServiceImpl#analyze(RequestDto)} корректно
     * обрабатывает случай, когда анализ возвращает пустой результат (без слов и без ошибок).
     */
    @Test
    void analyze_whenEmptyResult_shouldReturnResponseWithEmptyLists() {
        RequestDto request = new RequestDto("./emptyDir", 3, 10, null, null, ProcessingMode.MULTI, 2);
        AnalysisResult emptyResult = new AnalysisResult(List.of(), List.of(), 0, 0L);
        when(fileAnalysisServiceImpl.performAnalysis(request)).thenReturn(emptyResult);

        ResponseDto response = textAnalyzeService.analyze(request);

        assertThat(response.words()).isEmpty();
        assertThat(response.errors()).isEmpty();
        assertThat(response.infoDto().processedFiles()).isZero();
    }

    /**
     * Проверяет, что время выполнения в {@link InfoDto} всегда положительное
     * (или ноль, если анализ выполнился мгновенно).
     */
    @Test
    void analyze_shouldMeasureExecutionTime() {
        RequestDto request = new RequestDto("./dir", 3, 10, null, null, ProcessingMode.MULTI, 2);
        AnalysisResult result = new AnalysisResult(List.of(), List.of(), 0, 0L);
        when(fileAnalysisServiceImpl.performAnalysis(request)).thenReturn(result);

        long start = System.currentTimeMillis();
        ResponseDto response = textAnalyzeService.analyze(request);
        long end = System.currentTimeMillis();

        long executionTime = response.infoDto().executionTimeMs();
        assertThat(executionTime).isBetween(0L, end - start + 1);
    }
}