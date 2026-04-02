package io.ylab.wordflow.service.analysis.impl;

import io.ylab.wordflow.dto.*;
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
import java.util.concurrent.ConcurrentHashMap;
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

    @Override
    public Optional<ResponseDto> analyze(RequestDto requestDto) {
        logger.info("Starting directory analysis");

        Set<String> stopWords = loadStopWords(requestDto.stopWordsFile());
        List<ErrorDto> errors = Collections.synchronizedList(new ArrayList<>());

        try {
            directoryValidator.validate(requestDto.directory());

        } catch (IllegalArgumentException e) {
            logger.error("Directory: {} not valid", requestDto.directory());
            return Optional.empty();
        }

        Map<String, Integer> incomingWordCount = getIncomingWordWithCount(requestDto, stopWords, errors);
        List<Map.Entry<String, Integer>> topWords = aggregateWords(incomingWordCount, requestDto.top());

        List<WordCountDto> words = topWords.stream()
                .map(entry -> new WordCountDto(entry.getKey(), entry.getValue()))
                .toList();

        InfoDto infoDto = new InfoDto(requestDto.directory(), requestDto.minWordLength(), requestDto.top());

        ResponseDto responseDto = new ResponseDto(infoDto, words, errors);

        return Optional.of(responseDto);
    }

    private Set<String> loadStopWords(String file){
        if (file == null || file.isBlank() || !fileValidator.isValid(file)) {
            logger.info("Load empty stopwords");
            return Collections.emptySet();
        } else {
            return new HashSet<>(fileReader.readWords(file));
        }
    }

    private Map<String, Integer> getIncomingWordWithCount(RequestDto requestDto, Set<String> stopWords, List<ErrorDto> errors){
        Path dir = Paths.get(requestDto.directory());
        Map<String, Integer> mapWordCount = new ConcurrentHashMap<>();
        logger.info("Scan directory: {}", dir);

        try (Stream<Path> pathes = Files.list(dir)){
            List<Path> files = pathes
                    .filter(Files::isRegularFile)
                    .filter(file -> file.toString().endsWith(".txt"))
                    .filter(file -> {
                        try{
                            fileValidator.validate(file.toString());
                            return true;
                        } catch (IllegalArgumentException e){
                            logger.error("File {} not valid: {}", file, e.getMessage());
                            errors.add(new ErrorDto(file.toString(), e.getMessage()));
                            return false;
                        }
                    })
                    .toList();

            if (files.isEmpty()){
                logger.info("Not found files .txt");
                return mapWordCount;
            }

            files.parallelStream().forEach(file ->
                    fileReader.readWords(file.toString())
                            .stream().filter(
                                    word -> word.length() >= requestDto.minWordLength()
                            )
                            .filter(word -> !stopWords.contains(word))
                            .forEach(word -> mapWordCount.merge(word, 1, Integer::sum)));

        } catch (IOException e) {
            logger.error("Fail read dir: {}", requestDto.directory());
            throw new RuntimeException("Fail read dir: " + requestDto.directory(), e);
        }

        return mapWordCount;
    }

    private List<Map.Entry<String, Integer>> aggregateWords(Map<String, Integer> mapWordCount, Integer top){
        if (mapWordCount.isEmpty()) {
            logger.info("No words for aggregate");
            return Collections.emptyList();
        }
        return mapWordCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(top)
                .toList();
    }

}
