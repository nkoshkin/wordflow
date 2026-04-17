package io.ylab.wordflow.core.processor.impl;

import io.ylab.wordflow.core.processor.IFileProcessor;
import io.ylab.wordflow.dto.ErrorDto;
import io.ylab.wordflow.core.readers.IReader;
import io.ylab.wordflow.dto.WordCountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Реализация {@link IFileProcessor}, поддерживающая последовательную и параллельную обработку файлов.
 * Использует {@link IReader} для чтения слов и {@link ConcurrentHashMap} для потокобезопасного слияния результатов.
 *
 * <p>При {@code threads <= 1} файлы обрабатываются последовательно в текущем потоке.
 * При {@code threads > 1} создаётся пул фиксированного размера и файлы обрабатываются параллельно.</p>
 *
 * @see IFileProcessor
 * @see IReader
 */
@Component
public class FileProcessorImpl implements IFileProcessor {

    @Autowired
    IReader reader;

    /**
     * {@inheritDoc}
     * Обрабатывает список файлов и возвращает топ слов.
     *
     * <p>В зависимости от значения {@code threads} выбирается стратегия:
     * <ul>
     *   <li><b>threads = 1</b> – простой цикл по файлам.</li>
     *   <li><b>threads > 1</b> – пул потоков фиксированного размера, каждый файл обрабатывается в отдельной задаче.</li>
     * </ul>
     * </p>
     *
     * @param files список файлов
     * @param minWordLength минимальная длина слова
     * @param stopWords стоп-слова
     * @param errors список для ошибок (потокобезопасный)
     * @param threads количество потоков
     * @param top количество топ слов
     * @return список топ слов (отсортированный)
     */
    @Override
    public List<WordCountDto> processFiles(List<Path> files,
                                           Integer minWordLength,
                                           Set<String> stopWords,
                                           List<ErrorDto> errors,
                                           Integer threads,
                                           Integer top) {
        Map<String, Integer> wordCounts = new ConcurrentHashMap<>();
        execute(files, file -> {
            FileProcessResult result = processSingleFile(file, minWordLength, stopWords);
            if (result.hasError()) {
                synchronized (errors) {
                    errors.add(result.error);
                }
            } else {
                result.getWordCounts().forEach((k,v) -> wordCounts.merge(k, v, Integer::sum));
            }
        }, threads);
        return wordCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(top)
                .map(e -> new WordCountDto(e.getKey(), e.getValue()))
                .toList();
    }

    /**
     * Выполняет переданную задачу для каждого файла из списка.
     * При {@code threads <= 1} задача выполняется последовательно в текущем потоке.
     * При {@code threads > 1} создается пул фиксированного размера, и задачи выполняются параллельно
     * с использованием {@link CompletableFuture}.
     *
     * @param files   список файлов для обработки
     * @param task    задача, принимающая путь к файлу (не должна выбрасывать непроверяемые исключения,
     *                так как они перехватываются внутри)
     * @param threads количество потоков (1 – последовательно, >1 – параллельно)
     */
    private void execute(List<Path> files, Consumer<Path> task, Integer threads){
        if (threads <= 1) {
            files.forEach(task);
        } else {
            try (ExecutorService executor = Executors.newFixedThreadPool(threads)) {
                List<CompletableFuture<Void>> futures = files.stream()
                        .map(file -> CompletableFuture.runAsync(() -> task.accept(file), executor))
                        .toList();
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            }
        }
    }

    /**
     * Обрабатывает один файл: читает слова, фильтрует по минимальной длине и стоп-словам,
     * подсчитывает частоту и возвращает результат в виде {@link FileProcessResult}.
     *
     * <p>В случае успеха возвращается объект с картой частот, при ошибке – с объектом {@link ErrorDto}.</p>
     *
     * @param file          путь к файлу
     * @param minWordLength минимальная длина слова
     * @param stopWords     множество стоп-слов
     * @return результат обработки файла (успех или ошибка)
     */
    private FileProcessResult processSingleFile(Path file, int minWordLength, Set<String> stopWords) {
        try {
            List<String> words = reader.readWords(file.toString());
            Map<String, Integer> localCounts = new HashMap<>();
            for (String w : words) {
                if (w.length() >= minWordLength && !stopWords.contains(w)) {
                    localCounts.merge(w, 1, Integer::sum);
                }
            }
            return FileProcessResult.success(localCounts);
        } catch (Exception e) {
            return FileProcessResult.error(new ErrorDto(file.toString(), e.getMessage()));
        }
    }

    /**
     * Результат обработки одного файла.
     *
     * @param wordCounts карта слово → частота (не {@code null} при успехе)
     * @param error информация об ошибке (не {@code null} при ошибке)
     */
    private record FileProcessResult(Map<String, Integer> wordCounts, ErrorDto error) {
        static FileProcessResult success(Map<String, Integer> counts) {
            return new FileProcessResult(counts, null);
        }
        static FileProcessResult error(ErrorDto err) {
            return new FileProcessResult(null, err);
        }
        boolean hasError() {
            return error != null;
        }
        Map<String, Integer> getWordCounts() {
            return wordCounts != null ? wordCounts : Map.of();
        }
    }
}
