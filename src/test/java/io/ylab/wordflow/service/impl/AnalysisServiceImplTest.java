package io.ylab.wordflow.service.impl;

import io.ylab.wordflow.dto.AnalysisResult;
import io.ylab.wordflow.dto.ErrorDto;
import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.dto.WordCountDto;
import io.ylab.wordflow.entity.AnalysisEntity;
import io.ylab.wordflow.enums.AnalysisStatus;
import io.ylab.wordflow.enums.ProcessingMode;
import io.ylab.wordflow.repository.AnalysisRepository;
import io.ylab.wordflow.service.IAuditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Тесты для {@link AnalysisServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class AnalysisServiceImplTest {

    @Mock
    private AnalysisRepository analysisRepository;

    @Mock
    private IAuditService auditService;

    @InjectMocks
    private AnalysisServiceImpl analysisService;

    /**
     * Проверяет, что {@link AnalysisServiceImpl#startAnalysis(RequestDto)} сохраняет сущность,
     * логирует аудит и возвращает UUID.
     */
    @Test
    void startAnalysis() {
        RequestDto request = new RequestDto("./dir", 3, 10, null, null, ProcessingMode.MULTI, 2);
        UUID expectedId = UUID.randomUUID();
        AnalysisEntity savedEntity = AnalysisEntity.builder()
                .id(expectedId)
                .directory(request.directory())
                .minLength(request.minLength())
                .top(request.top())
                .mode(request.mode().name().toLowerCase())
                .threads(request.threads())
                .stopWordsFile(request.stopWordsFile())
                .outputFile(request.outputFile())
                .status(AnalysisStatus.PENDING)
                .startTime(LocalDateTime.now())
                .build();

        when(analysisRepository.save(any(AnalysisEntity.class))).thenReturn(savedEntity);

        UUID result = analysisService.startAnalysis(request);

        assertThat(result).isEqualTo(expectedId);
        verify(analysisRepository, times(1)).save(any(AnalysisEntity.class));
        verify(auditService, times(1)).log(eq("START_ANALYZE"), anyString(), eq(expectedId));
    }

    /**
     * Проверяет, что при существующем анализе метод {@link AnalysisServiceImpl#getAnalysis(UUID)}
     * возвращает сущность.
     */
    @Test
    void getAnalysis_whenExists() {
        UUID id = UUID.randomUUID();
        AnalysisEntity entity = AnalysisEntity.builder().directory("./dir").id(id).build();
        when(analysisRepository.findById(id)).thenReturn(Optional.of(entity));

        AnalysisEntity result = analysisService.getAnalysis(id);

        assertThat(result).isEqualTo(entity);
        assertThat(result.getId()).isEqualTo(id);
        verify(analysisRepository, times(1)).findById(id);
    }

    /**
     * Проверяет, что при отсутствии анализа метод {@link AnalysisServiceImpl#getAnalysis(UUID)}
     * выбрасывает {@link ResponseStatusException} с кодом 404.
     */
    @Test
    void getAnalysis_whenNotExists() {
        UUID id = UUID.randomUUID();
        when(analysisRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> analysisService.getAnalysis(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Analysis not found");
        verify(analysisRepository, times(1)).findById(id);
    }

    /**
     * Проверяет, что {@link AnalysisServiceImpl#getAllAnalysis()} ()} возвращает список всех анализов,
     * отсортированных по времени начала (новые первыми).
     */
    @Test
    void getAllAnalyses_shouldReturnSortedList() {
        List<AnalysisEntity> expectedList = List.of(
                AnalysisEntity.builder().id(UUID.randomUUID()).directory("/dir1").build(),
                AnalysisEntity.builder().id(UUID.randomUUID()).directory("/dir2").build()
        );
        when(analysisRepository.findAllByOrderByStartTimeDesc()).thenReturn(expectedList);

        List<AnalysisEntity> result = analysisService.getAllAnalysis();

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedList);
        verify(analysisRepository, times(1)).findAllByOrderByStartTimeDesc();
    }

    /**
     * Проверяет, что {@link AnalysisServiceImpl#saveAnalysisResult(UUID, AnalysisResult)}
     * корректно обновляет сущность: добавляет слова, ошибки, статистику и устанавливает статус COMPLETED.
     */
    @Test
    void saveAnalysisResult_shouldUpdateEntityWithWordsAndErrorsAndSetCompleted() {
        UUID analysisId = UUID.randomUUID();
        AnalysisEntity entity = AnalysisEntity.builder()
                .id(analysisId)
                .words(new ArrayList<>())
                .errors(new ArrayList<>())
                .processedFiles(0)
                .executionTimeMs(0L)
                .build();
        when(analysisRepository.findById(analysisId)).thenReturn(Optional.of(entity));

        AnalysisResult result = new AnalysisResult(
                List.of(new WordCountDto("word", 10), new WordCountDto("test", 5)),
                List.of(new ErrorDto("error.txt", "File not found")),
                2,
                123L
        );

        analysisService.saveAnalysisResult(analysisId, result);

        assertThat(entity.getWords()).hasSize(2);
        assertThat(entity.getWords().getFirst().getWord()).isEqualTo("word");
        assertThat(entity.getWords().getFirst().getCount()).isEqualTo(10);
        assertThat(entity.getErrors()).hasSize(1);
        assertThat(entity.getErrors().getFirst().getFile()).isEqualTo("error.txt");
        assertThat(entity.getProcessedFiles()).isEqualTo(2);
        assertThat(entity.getExecutionTimeMs()).isEqualTo(123L);
        assertThat(entity.getStatus()).isEqualTo(AnalysisStatus.COMPLETED);
        assertThat(entity.getEndTime()).isNotNull();

        verify(analysisRepository, never()).save(any());
    }

    /**
     * Проверяет, что при фатальной ошибке (отсутствие директории)
     * статус анализа устанавливается в {@link AnalysisStatus#FAILED}.
     */
    @Test
    void saveAnalysisResult_whenNoWordsAndHasDirectoryError_shouldSetFailedStatus() {
        UUID analysisId = UUID.randomUUID();
        AnalysisEntity entity = AnalysisEntity.builder()
                .id(analysisId)
                .words(new ArrayList<>())
                .errors(new ArrayList<>())
                .processedFiles(0)
                .executionTimeMs(0L)
                .build();
        when(analysisRepository.findById(analysisId)).thenReturn(Optional.of(entity));

        AnalysisResult result = new AnalysisResult(
                List.of(),
                List.of(new ErrorDto("./invalid", "Directory does not exist")),
                0,
                10L
        );

        analysisService.saveAnalysisResult(analysisId, result);

        assertThat(entity.getWords()).isEmpty();
        assertThat(entity.getErrors()).hasSize(1);
        assertThat(entity.getErrors().getFirst().getFile()).isEqualTo("./invalid");
        assertThat(entity.getErrors().getFirst().getMessage()).contains("Directory does not exist");
        assertThat(entity.getProcessedFiles()).isZero();
        assertThat(entity.getExecutionTimeMs()).isEqualTo(10L);
        assertThat(entity.getStatus()).isEqualTo(AnalysisStatus.FAILED);
        assertThat(entity.getEndTime()).isNotNull();
        verify(analysisRepository, never()).save(any());
    }

    /**
     * Проверяет, что при отсутствии слов и наличии нефатальных ошибок
     * статус остаётся {@link AnalysisStatus#COMPLETED}.
     */
    @Test
    void saveAnalysisResult_whenNoWordsButNonFatalErrors_shouldSetCompleted() {
        UUID analysisId = UUID.randomUUID();
        AnalysisEntity entity = AnalysisEntity.builder()
                .id(analysisId)
                .words(new ArrayList<>())
                .errors(new ArrayList<>())
                .processedFiles(0)
                .executionTimeMs(0L)
                .build();
        when(analysisRepository.findById(analysisId)).thenReturn(Optional.of(entity));

        AnalysisResult result = new AnalysisResult(
                List.of(),
                List.of(new ErrorDto("file.txt", "Access denied")),
                0,
                10L
        );

        analysisService.saveAnalysisResult(analysisId, result);

        assertThat(entity.getWords()).isEmpty();
        assertThat(entity.getErrors()).hasSize(1);
        assertThat(entity.getStatus()).isEqualTo(AnalysisStatus.COMPLETED);
        verify(analysisRepository, never()).save(any());
    }

    /**
     * Проверяет, что при пустом результате (нет слов и нет ошибок) статус COMPLETED.
     */
    @Test
    void saveAnalysisResult_whenEmptyResult_shouldSetCompleted() {
        UUID analysisId = UUID.randomUUID();
        AnalysisEntity entity = AnalysisEntity.builder()
                .id(analysisId)
                .words(new ArrayList<>())
                .errors(new ArrayList<>())
                .processedFiles(0)
                .executionTimeMs(0L)
                .build();
        when(analysisRepository.findById(analysisId)).thenReturn(Optional.of(entity));

        AnalysisResult result = new AnalysisResult(List.of(), List.of(), 0, 0L);

        analysisService.saveAnalysisResult(analysisId, result);

        assertThat(entity.getWords()).isEmpty();
        assertThat(entity.getErrors()).isEmpty();
        assertThat(entity.getStatus()).isEqualTo(AnalysisStatus.COMPLETED);
        verify(analysisRepository, never()).save(any());
    }

}