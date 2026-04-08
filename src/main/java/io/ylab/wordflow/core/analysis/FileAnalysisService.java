package io.ylab.wordflow.core.analysis;

import io.ylab.wordflow.core.readers.Ireader;
import io.ylab.wordflow.core.validator.impl.DirectoryValidator;
import io.ylab.wordflow.core.validator.impl.FileValidator;
import io.ylab.wordflow.dto.AnalysisResult;
import io.ylab.wordflow.dto.ErrorDto;
import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.dto.WordCountDto;
import io.ylab.wordflow.processor.FileProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileAnalysisService {

    private final FileProcessor fileProcessor;
    private final Ireader ireader;
    private final DirectoryValidator directoryValidator;
    private final FileValidator fileValidator;

    public AnalysisResult performAnalysis(RequestDto request) {

        try {
            directoryValidator.validate(request.directory());
        } catch (IllegalArgumentException e) {
            return new AnalysisResult(
                    List.of(),
                    List.of(new ErrorDto(request.directory(), e.getMessage())),
                    0
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

        return new AnalysisResult(wordCounts, errors, allFiles.size());
    }

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

    private List<Path> validateFiles(List<Path> files, List<ErrorDto> errors) {
        return files.stream()
                .filter(file -> {
                    try {
                        fileValidator.validate(file.toString());
                        return true;
                    } catch (IllegalArgumentException e) {
                        log.error("File validation failed: {} - {}", file, e.getMessage());
                        errors.add(new ErrorDto(file.toString(), e.getMessage()));
                        return false;
                    }
                })
                .toList();
    }

    private Set<String> loadStopWords(String file){
        if (file == null || file.isBlank() || !fileValidator.isValid(file)) {
            log.info("Load empty stopwords");
            return Collections.emptySet();
        } else {
            return new HashSet<>(ireader.readWords(file));
        }
    }

    private List<Path> collectAndValidateFiles(String directory, List<ErrorDto> errors) {
        List<Path> allFiles = collectFiles(directory);
        return validateFiles(allFiles, errors);
    }
}
