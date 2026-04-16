package io.ylab.wordflow.core.processor;

import io.ylab.wordflow.dto.ErrorDto;
import io.ylab.wordflow.dto.WordCountDto;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public interface IFileProcessor {

    List<WordCountDto> processFiles(List<Path> files,
                                    Integer minWordLength,
                                    Set<String> stopWords,
                                    List<ErrorDto> errors,
                                    Integer threads,
                                    Integer top);
}
