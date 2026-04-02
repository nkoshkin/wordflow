package io.ylab.wordflow.runner;

import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.dto.ResponseDto;
import io.ylab.wordflow.service.analysis.ITextAnalysis;
import io.ylab.wordflow.service.arguments.IRequestService;
import io.ylab.wordflow.service.helper.IHelper;
import io.ylab.wordflow.service.output.OutputService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WordFlowRunner implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(WordFlowRunner.class);

    @Autowired
    IRequestService cliRequestServiceImpl;
    @Autowired
    IHelper helper;
    @Autowired
    ITextAnalysis textAnalysis;
    @Autowired
    OutputService outputService;

    @Override
    public void run(ApplicationArguments args) {
        Optional<RequestDto> optionalRequestDto = cliRequestServiceImpl.parse();
        if (optionalRequestDto.isEmpty()){
            helper.help();
            return;
        }
        RequestDto requestDto = optionalRequestDto.get();

        Optional<ResponseDto> optionalResponseDto = textAnalysis.analyze(requestDto);

        if (optionalResponseDto.isPresent()) {
            outputService.returnResponse(optionalResponseDto.get(), requestDto.outputFile());
            logger.info("Success execute job");
        } else {
            logger.error("Failed execute job");
        }

    }
}
