package io.ylab.wordflow.core.service.impl;

import io.ylab.wordflow.core.processor.IFileProcessor;
import io.ylab.wordflow.core.readers.IReader;
import io.ylab.wordflow.core.service.IFileAnalysisService;
import io.ylab.wordflow.core.validator.impl.DirectoryValidatorImpl;
import io.ylab.wordflow.core.validator.impl.FileValidatorImpl;
import io.ylab.wordflow.dto.AnalysisResult;
import io.ylab.wordflow.dto.ErrorDto;
import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.dto.WordCountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * Реализация сервиса анализа текстов.
 * Содержит всю бизнес-логику: валидацию, сбор файлов, обработку и формирование результата.
 *
 * <p>Класс не зависит от базы данных, поэтому может использоваться как в REST,
 * так и в CLI-режиме.</p>
 *
 * @see IFileAnalysisService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileAnalysisServiceImpl implements IFileAnalysisService {

    private final IFileProcessor fileProcessor;
    private final IReader ireader;
    private final DirectoryValidatorImpl directoryValidatorImpl;
    private final FileValidatorImpl fileValidatorImpl;

    /**
     * Выполняет полный анализ директории.
     *
     * <p>Метод замеряет время выполнения и возвращает объект {@link AnalysisResult},
     * содержащий топ-слова, ошибки, количество обработанных файлов и время выполнения.</p>
     *
     * @param request параметры анализа (не может быть {@code null})
     * @return результат анализа
     * @throws IllegalArgumentException если директория не существует или недоступна
     */
    @Override
    public AnalysisResult performAnalysis(RequestDto request) {
        long startTime = System.currentTimeMillis();

        try {
            directoryValidatorImpl.validate(request.directory());
        } catch (IllegalArgumentException e) {
            return new AnalysisResult(
                    List.of(),
                    List.of(new ErrorDto(request.directory(), e.getMessage())),
                    0, System.currentTimeMillis() - startTime
            );
        }

        List<ErrorDto> errors = Collections.synchronizedList(new ArrayList<>());
        List<Path> allFiles = collectAndValidateFiles(request.directory(), errors);
        Set<String> stopWords = loadStopWords(request.stopWordsFile());

        List<WordCountDto> wordCounts = fileProcessor.processFiles(
                allFiles,
                request.minLength(),
                stopWords,
                errors,
                request.threads(),
                request.top()
        );

        return new AnalysisResult(wordCounts, errors, allFiles.size(), System.currentTimeMillis() - startTime);
    }

    /**
     * Возвращает список всех файлов .txt в директории.
     *
     * @param directory путь к директории
     * @return список путей к файлам
     */
    private List<Path> collectFiles(String directory){
        Path dir = Paths.get(directory);
        try (Stream<Path> paths = Files.list(dir)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt"))
                    .toList();
        } catch (IOException e) {
            log.error("Failed read directory: {}", directory, e);
            return List.of();
        }
    }

    /**
     * Фильтрует список файлов, оставляя только валидные, и заполняет список ошибок.
     *
     * @param files  список файлов для проверки
     * @param errors список для накопления ошибок
     * @return список валидных файлов
     */
    private List<Path> validateFiles(List<Path> files, List<ErrorDto> errors) {
        return files.stream()
                .filter(file -> {
                    try {
                        fileValidatorImpl.validate(file.toString());
                        return true;
                    } catch (IllegalArgumentException e) {
                        log.error("File validation failed: {} - {}", file, e.getMessage());
                        errors.add(new ErrorDto(file.toString(), e.getMessage()));
                        return false;
                    }
                })
                .toList();
    }

    /**
     * Загружает стоп-слова из файла (если указан).
     *
     * @param file путь к файлу стоп-слов (может быть {@code null})
     * @return множество стоп-слов (пустое, если файл не указан или не может быть прочитан)
     */
    private Set<String> loadStopWords(String file){
        if (file == null || file.isBlank() || !fileValidatorImpl.isValid(file)) {
            log.info("Load empty stopwords");
            return Collections.emptySet();
        } else {
            return new HashSet<>(ireader.readWords(file));
        }
    }

    /**
     * Собирает все файлы .txt из директории и возвращает только валидные,
     * заполняя список ошибок.
     *
     * @param directory путь к директории
     * @param errors    список для накопления ошибок (будет изменён)
     * @return список валидных файлов {@link Path}
     */
    private List<Path> collectAndValidateFiles(String directory, List<ErrorDto> errors) {
        List<Path> allFiles = collectFiles(directory);
        return validateFiles(allFiles, errors);
    }
}
