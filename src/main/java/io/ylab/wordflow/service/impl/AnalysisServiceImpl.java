package io.ylab.wordflow.service.impl;

import io.ylab.wordflow.dto.AnalysisResult;
import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.entity.AnalysisEntity;
import io.ylab.wordflow.entity.ErrorEntity;
import io.ylab.wordflow.entity.WordCountEntity;
import io.ylab.wordflow.enums.AnalysisStatus;
import io.ylab.wordflow.repository.AnalysisRepository;
import io.ylab.wordflow.service.IAnalysisService;
import io.ylab.wordflow.service.IAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления анализами.
 * Содержит синхронные операции для работы с анализами (создание, сохранение, получение).
 *
 * <p>Методы, изменяющие состояние, обёрнуты в {@code @Transactional}.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements IAnalysisService {

    private final AnalysisRepository analysisRepository;
    private final IAuditService auditServiceImpl;

    /**
     * {@inheritDoc}
     *
     * <p>Создаёт запись анализа со статусом {@code PENDING}, сохраняет её,
     * логирует аудит и возвращает UUID. Асинхронная обработка запускается отдельно.</p>
     *
     * @param request параметры анализа
     * @return UUID созданного анализа
     */
    @Override
    @Transactional
    public UUID startAnalysis(RequestDto request) {
        AnalysisEntity entity = AnalysisEntity.builder()
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

        entity = analysisRepository.save(entity);
        UUID id = entity.getId();
        auditServiceImpl.log("START_ANALYZE", request.toString(), id);
        log.info("Analysis started with id: {}", id);
        return id;
    }

    /**
     * {@inheritDoc}
     *
     * @param id идентификатор анализа
     * @return сущность анализа
     * @throws ResponseStatusException если анализ не найден (404)
     */
    @Override
    public AnalysisEntity getAnalysis(UUID id) {
        return analysisRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Analysis not found"));
    }

    /**
     * {@inheritDoc}
     *
     * @return список всех анализов, отсортированных по времени начала (новые первыми)
     */
    @Override
    public List<AnalysisEntity> getAllAnalysis() {
        return analysisRepository.findAllByOrderByStartTimeDesc();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Обновляет сущность анализа, устанавливает слова, ошибки,
     * количество обработанных файлов, время выполнения и статус
     * ({@code COMPLETED} или {@code FAILED}).</p>
     *
     * @param analysisId идентификатор анализа
     * @param result     результат анализа
     * @throws RuntimeException если анализ не найден
     */
    @Override
    @Transactional
    public void saveAnalysisResult(UUID analysisId, AnalysisResult result) {
        AnalysisEntity analysis = analysisRepository
                .findById(analysisId)
                .orElseThrow(() -> new RuntimeException("Analysis not found"));

        if (!result.wordsCount().isEmpty()) {
            analysis.setWords(result.wordsCount().stream()
                    .map(wc -> WordCountEntity.builder()
                            .analysis(analysis)
                            .word(wc.word())
                            .count(wc.count())
                            .build())
                    .collect(Collectors.toList()));
        }
        if (!result.errors().isEmpty()) {
            analysis.setErrors(result.errors().stream()
                    .map(e -> ErrorEntity.builder()
                            .analysis(analysis)
                            .file(e.file())
                            .message(e.message())
                            .build())
                    .collect(Collectors.toList()));
        }
        AnalysisStatus status = getStatusFromresult(result);
        analysis.setProcessedFiles(result.processedFiles());
        analysis.setStatus(status);
        analysis.setEndTime(LocalDateTime.now());
        analysis.setExecutionTimeMs(result.executionTime());

    }

    /**
     * Определяет итоговый статус анализа на основе списка ошибок.
     *
     * <p>Метод проверяет, содержится ли среди ошибок фатальная ошибка,
     * связанная с несуществующей директорией. Если такая ошибка присутствует,
     * анализ считается провалившимся (статус {@link AnalysisStatus#FAILED}),
     * иначе – успешно завершённым ({@link AnalysisStatus#COMPLETED}).
     * </p>
     * <p><b>Примечание:</b> Метод предполагает, что список ошибок не пуст. Если он пуст,
     * результат будет {@code COMPLETED} (так как условие не выполнится).</p>
     *
     * @param result результат анализа, содержащий список ошибок ({@link AnalysisResult#errors()})
     * @return {@link AnalysisStatus#FAILED}, если обнаружена фатальная ошибка директории,
     *         иначе {@link AnalysisStatus#COMPLETED}
     */
    private AnalysisStatus getStatusFromresult(AnalysisResult result) {
        boolean hasDirectoryError = result.errors().stream()
                .anyMatch(e -> e.file().equals(result.errors().getFirst().file()) &&
                        e.message().contains("Directory does not exist"));

        return (hasDirectoryError) ? AnalysisStatus.FAILED : AnalysisStatus.COMPLETED;
    }
}