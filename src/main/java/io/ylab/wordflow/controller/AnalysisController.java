package io.ylab.wordflow.controller;

import io.ylab.wordflow.audit.Auditable;
import io.ylab.wordflow.dto.AnalysisSummaryDto;
import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.dto.ResponseDto;
import io.ylab.wordflow.entity.AnalysisEntity;
import io.ylab.wordflow.enums.AnalysisStatus;
import io.ylab.wordflow.mapper.AnalysisDtoMapper;
import io.ylab.wordflow.service.IAnalysisService;
import io.ylab.wordflow.service.IAsyncAnalysisExecutor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST-контроллер для управления анализами текстов.
 * Предоставляет эндпоинты для запуска анализа, получения результата и списка анализов.
 *
 * @see IAnalysisService
 * @see IAsyncAnalysisExecutor
 * @see AnalysisDtoMapper
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnalysisController {

    private final IAnalysisService analysisService;
    private final IAsyncAnalysisExecutor asyncAnalysisExecutor;
    private final AnalysisDtoMapper analysisDtoMapper;

    /**
     * Запускает новый анализ текстов.
     *
     * <p>Создаёт запись анализа в базе данных (статус {@code PENDING}),
     * асинхронно запускает обработку файлов и возвращает идентификатор анализа.
     * Сам анализ выполняется в фоновом потоке.</p>
     *
     * @param request параметры анализа
     * @return ответ с ID анализа и HTTP-статусом 202 (Accepted)
     */
    @PostMapping("/analyze")
    @Auditable(action = "START_ANALYSIS")
    public ResponseEntity<Map<String, UUID>> startAnalysis(@Valid @RequestBody RequestDto request) {
        UUID id = analysisService.startAnalysis(request);
        asyncAnalysisExecutor.execute(id, request);
        return ResponseEntity.accepted().body(Map.of("id", id));
    }

    /**
     * Возвращает результат анализа по идентификатору.
     *
     * <p>Если анализ ещё не завершён (статус {@code PENDING} или {@code RUNNING}),
     * возвращается краткая информация со статусом. Если завершён – возвращается
     * полный {@link ResponseDto} (метаинформация, топ-слова, ошибки).</p>
     *
     * @param id идентификатор анализа (UUID)
     * @return статус выполнения или полный результат
     * @throws ResponseStatusException если анализ с указанным ID не найден (404)
     */
    @GetMapping("/results/{id}")
    public ResponseEntity<?> getResult(@PathVariable UUID id) {
        try {
            AnalysisEntity entity = analysisService.getAnalysis(id);
            if (entity.getStatus() != AnalysisStatus.COMPLETED) {
                return ResponseEntity.ok(Map.of(
                        "id", entity.getId().toString(),
                        "status", entity.getStatus(),
                        "startTime", entity.getStartTime()
                ));
            }
            return ResponseEntity.ok(analysisDtoMapper.toResponseDto(entity));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getReason() != null ? e.getReason() : "Unknown error"));
        }
    }

    /**
     * Возвращает список всех анализов с краткой информацией.
     *
     * <p>Каждый элемент содержит ID, статус, время начала и путь к директории.
     * Список отсортирован по времени начала от новых к старым.</p>
     *
     * @return список {@link AnalysisSummaryDto}
     */
    @GetMapping("/results")
    @Auditable(action = "LIST_RESULTS")
    public ResponseEntity<List<AnalysisSummaryDto>> listResults() {
        List<AnalysisSummaryDto> list = analysisService.getAllAnalysis().stream()
                .map(a -> new AnalysisSummaryDto(
                        a.getId(),
                        a.getStatus(),
                        a.getStartTime(),
                        a.getDirectory()
                ))
                .toList();
        return ResponseEntity.ok(list);
    }
}
