package io.ylab.wordflow.runner;

import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.dto.ResponseDto;
import io.ylab.wordflow.service.analysis.ITextAnalysis;
import io.ylab.wordflow.service.arguments.IRequestService;
import io.ylab.wordflow.service.helper.IHelper;
import io.ylab.wordflow.service.output.OutputService;
import io.ylab.wordflow.service.properties.IPropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

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
    @Autowired
    IPropertyService propertyService;


    @Override
    public void run(ApplicationArguments args) {

        if (propertyService.hasParam("help")){
            helper.help();
            return;
        }
        RequestDto request = cliRequestServiceImpl.parse();
        logger.info("get request comlete");
        ResponseDto response = textAnalysis.analyze(request);
        logger.info("get response comlete");
        outputService.returnResponse(response, request.outputFile());

    }
}
