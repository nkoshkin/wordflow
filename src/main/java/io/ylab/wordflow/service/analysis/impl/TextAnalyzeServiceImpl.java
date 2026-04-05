package io.ylab.wordflow.service.analysis.impl;

import io.ylab.wordflow.dto.*;
import io.ylab.wordflow.processor.FileProcessor;
import io.ylab.wordflow.service.analysis.ITextAnalysis;
import io.ylab.wordflow.service.readers.impl.FileReaderImpl;
import io.ylab.wordflow.service.validator.impl.DirectoryValidator;
import io.ylab.wordflow.service.validator.impl.FileValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Service
public class TextAnalyzeServiceImpl implements ITextAnalysis {

    private static final Logger logger = LoggerFactory.getLogger(TextAnalyzeServiceImpl.class);

    @Autowired
    DirectoryValidator directoryValidator;

    @Autowired
    FileValidator fileValidator;

    @Autowired
    FileReaderImpl fileReader;

    @Autowired
    FileProcessor fileProcessor;

    @Override
    public ResponseDto analyze(RequestDto requestDto) {
        long startTime = System.currentTimeMillis();
        long executionTime;
        logger.info("Starting directory analysis");

        Set<String> stopWords = loadStopWords(requestDto.stopWordsFile());
        List<ErrorDto> errors = Collections.synchronizedList(new ArrayList<>());

        try {
            directoryValidator.validate(requestDto.directory());
        } catch (IllegalArgumentException e) {
            logger.error("Directory: {} not valid", requestDto.directory());
            errors.add(new ErrorDto(requestDto.directory(), e.getMessage()));
            executionTime = System.currentTimeMillis() - startTime;
            return buildResponse(requestDto, Collections.emptyMap(), errors, 0, executionTime);
        }

        List<Path> validFiles = collectAndValidateFiles(requestDto.directory(), errors);

        if (validFiles.isEmpty()){
            executionTime = System.currentTimeMillis() - startTime;
            return buildResponse(requestDto, Collections.emptyMap(), errors, 0, executionTime);
        }

        Map<String, Integer> wordCounts = fileProcessor.processFiles(
                validFiles,
                requestDto.minWordLength(),
                stopWords,
                errors,
                requestDto.threads());

        executionTime = System.currentTimeMillis() - startTime;
        return buildResponse(requestDto, wordCounts, errors, validFiles.size(), executionTime);
    }

    private ResponseDto buildResponse(RequestDto requestDto, Map<String, Integer> wordCounts, List<ErrorDto> errors, int processedFiles, long executionTime) {
        List<WordCountDto> words = aggregateWords(wordCounts, requestDto.top());
        InfoDto info = new InfoDto(
                requestDto.directory(),
                requestDto.minWordLength(),
                requestDto.top(),
                requestDto.mode().name().toLowerCase(),
                requestDto.threads(),
                processedFiles,
                executionTime
        );
        return new ResponseDto(info, words, errors);
    }

    private Set<String> loadStopWords(String file){
        if (file == null || file.isBlank() || !fileValidator.isValid(file)) {
            logger.info("Load empty stopwords");
            return Collections.emptySet();
        } else {
            return new HashSet<>(fileReader.readWords(file));
        }
    }

    private List<WordCountDto> aggregateWords(Map<String, Integer> mapWordCount, Integer top){
        if (mapWordCount.isEmpty()) {
            logger.info("No words for aggregate");
            return Collections.emptyList();
        }
        return mapWordCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(top)
                .map(e -> new WordCountDto(e.getKey(), e.getValue()))
                .toList();
    }

    private List<Path> collectFiles(String directory){
        Path dir = Paths.get(directory);
        try (Stream<Path> paths = Files.list(dir)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt"))
                    .toList();
        } catch (IOException e) {
            logger.error("Failed read directory: {}", directory, e);
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
                        logger.error("File validation failed: {} - {}", file, e.getMessage());
                        errors.add(new ErrorDto(file.toString(), e.getMessage()));
                        return false;
                    }
                })
                .toList();
    }

    private List<Path> collectAndValidateFiles(String directory, List<ErrorDto> errors) {
        List<Path> allFiles = collectFiles(directory);
        return validateFiles(allFiles, errors);
    }

}
