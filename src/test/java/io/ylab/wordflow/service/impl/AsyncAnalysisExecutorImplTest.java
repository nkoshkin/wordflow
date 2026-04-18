package io.ylab.wordflow.service.impl;

import io.ylab.wordflow.core.service.IFileAnalysisService;
import io.ylab.wordflow.dto.AnalysisResult;
import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.enums.ProcessingMode;
import io.ylab.wordflow.service.IAnalysisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

/**
 * Тесты для {@link AsyncAnalysisExecutorImpl}.
 */
@ExtendWith(MockitoExtension.class)
class AsyncAnalysisExecutorImplTest {

    @Mock
    private IFileAnalysisService fileAnalysisService;

    @Mock
    private IAnalysisService analysisService;

    @InjectMocks
    private AsyncAnalysisExecutorImpl executor;

    /**
     * Проверяет, что при успешном выполнении анализа метод {@link AsyncAnalysisExecutorImpl#execute(UUID, RequestDto)}
     * вызывает {@link IFileAnalysisService#performAnalysis(RequestDto)} и
     * {@link IAnalysisService#saveAnalysisResult(UUID, AnalysisResult)} с корректными аргументами.
     */
    @Test
    void execute_shouldCallPerformAnalysisAndSaveResult() {
        UUID analysisId = UUID.randomUUID();
        RequestDto request = new RequestDto("./dir", 3, 10, null, null, ProcessingMode.MULTI, 2);
        AnalysisResult mockResult = new AnalysisResult(List.of(), List.of(), 0, 0L);
        when(fileAnalysisService.performAnalysis(request)).thenReturn(mockResult);

        executor.execute(analysisId, request);

        verify(fileAnalysisService, times(1)).performAnalysis(request);
        verify(analysisService, times(1)).saveAnalysisResult(analysisId, mockResult);
    }

    /**
     * Проверяет, что при возникновении исключения в {@link IFileAnalysisService#performAnalysis(RequestDto)}
     * метод {@link IAnalysisService#saveAnalysisResult(UUID, AnalysisResult)} не вызывается.
     */
    @Test
    void execute_whenPerformAnalysisThrowsException_shouldNotSaveResult() {
        UUID analysisId = UUID.randomUUID();
        RequestDto request = new RequestDto("./dir", 3, 10, null, null, ProcessingMode.MULTI, 2);
        when(fileAnalysisService.performAnalysis(request)).thenThrow(new RuntimeException("Test error"));

        assertDoesNotThrow(() -> executor.execute(analysisId, request));
        verify(fileAnalysisService, times(1)).performAnalysis(request);
        verify(analysisService, never()).saveAnalysisResult(any(), any());
    }
}