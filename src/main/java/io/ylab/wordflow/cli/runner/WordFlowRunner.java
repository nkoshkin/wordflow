package io.ylab.wordflow.cli.runner;

import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.dto.ResponseDto;
import io.ylab.wordflow.cli.service.ITextAnalysis;
import io.ylab.wordflow.cli.arguments.IRequestService;
import io.ylab.wordflow.cli.helper.IHelper;
import io.ylab.wordflow.cli.output.OutputService;
import io.ylab.wordflow.cli.properties.IPropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("cli")
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
