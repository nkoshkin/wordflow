package io.ylab.wordflow.cli.arguments.impl;

import io.ylab.wordflow.enums.ProcessingMode;
import io.ylab.wordflow.configuration.WordFlowConfiguration;
import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.cli.arguments.IRequestService;
import io.ylab.wordflow.cli.properties.IPropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Реализация {@link IRequestService} для чтения параметров командной строки.
 * @see IRequestService
 * @see IPropertyService
 */
@Service
@Profile("cli")
public class CliRequestServiceImpl implements IRequestService {

    private static final Logger logger = LoggerFactory.getLogger(CliRequestServiceImpl.class);

    @Autowired
    IPropertyService propertyService;

    @Autowired
    WordFlowConfiguration config;

    /**
     * {@inheritDoc}
     *
     * @return {@link RequestDto} с параметрами или {@code null}, если запрошена справка
     * @throws IllegalArgumentException если отсутствует обязательный параметр
     */
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
