package io.ylab.wordflow.core.processor;

import io.ylab.wordflow.dto.ErrorDto;
import io.ylab.wordflow.core.readers.Ireader;
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

@Component
public class FileProcessorImpl implements IFileProcessor {

    @Autowired
    Ireader reader;

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
