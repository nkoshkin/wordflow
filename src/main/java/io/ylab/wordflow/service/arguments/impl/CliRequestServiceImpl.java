package io.ylab.wordflow.service.arguments.impl;

import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.service.arguments.IRequestService;
import io.ylab.wordflow.service.properties.IPropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CliRequestServiceImpl implements IRequestService {

    private static final Logger logger = LoggerFactory.getLogger(CliRequestServiceImpl.class);

    @Autowired
    IPropertyService validator;

    @Override
    public Optional<RequestDto> parse() {
        if (validator.hasParam("help")) {
            return Optional.empty();
        }

        try {
            String dir = validator.getRequiredString("dir");
            Integer minLength = validator.getRequiredInt("min-length");
            Integer top = validator.getRequiredInt("top");
            String output = validator.getString("output", null);
            String stopwords = validator.getString("stopwords", null);
            RequestDto requestDto = new RequestDto(dir, minLength, top, output, stopwords);
            logger.info("Successfully parsed request: {}", requestDto);
            return Optional.of(requestDto);

        } catch (IllegalArgumentException e) {
            logger.error("Failed to parse arguments: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
