package io.ylab.wordflow.service.arguments.impl;

import io.ylab.wordflow.configuration.ProcessingMode;
import io.ylab.wordflow.configuration.WordFlowConfiguration;
import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.service.arguments.IRequestService;
import io.ylab.wordflow.service.properties.IPropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CliRequestServiceImpl implements IRequestService {

    private static final Logger logger = LoggerFactory.getLogger(CliRequestServiceImpl.class);

    @Autowired
    IPropertyService propertyService;

    @Autowired
    WordFlowConfiguration config;

    @Override
    public RequestDto parse() {
        String dir = propertyService.getRequiredString("dir");
        Integer minLength = propertyService.getRequiredInt("min-length");
        Integer top = propertyService.getRequiredInt("top");
        String output = propertyService.getString("output", null);
        String stopwords = propertyService.getString("stopwords", null);
        ProcessingMode mode = propertyService.getEnum("mode", config.mode(), ProcessingMode.class);
        Integer threads = (mode == ProcessingMode.MULTI) ? propertyService.getInt("threads", config.threads()) : 1;

        RequestDto requestDto = new RequestDto(dir, minLength, top, output, stopwords, mode, threads);
        logger.info("Successfully parsed request: {}", requestDto);
        return requestDto;
    }
}
