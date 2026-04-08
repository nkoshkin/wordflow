package io.ylab.wordflow.cli.service;

import io.ylab.wordflow.core.analysis.FileAnalysisService;
import io.ylab.wordflow.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("cli")
@RequiredArgsConstructor
public class TextAnalyzeServiceImpl implements ITextAnalysis {

    private final FileAnalysisService fileAnalysisService;

    @Override
    public ResponseDto analyze(RequestDto request) {
        long startTime = System.currentTimeMillis();

        AnalysisResult result = fileAnalysisService.performAnalysis(request);

        long executionTime = System.currentTimeMillis() - startTime;

        InfoDto info = new InfoDto(
                request.directory(),
                request.minLength(),
                request.top(),
                request.mode().name().toLowerCase(),
                request.threads(),
                result.processedFiles(),
                executionTime
        );

        return new ResponseDto(info, result.wordsCount(), result.errors());
    }
}
