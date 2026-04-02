package io.ylab.wordflow.service.properties.impl;

import io.ylab.wordflow.service.properties.IPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
public class CliPropertyServiceImpl implements IPropertyService {
    @Autowired
    ApplicationArguments args;

    @Override
    public <T> T getValue(String param, T defaultValue, Function<String, T> converter) {
        List<String> values = args.getOptionValues(param);
        String value = (values != null && !values.isEmpty())? values.getFirst() : null;
        if (value == null || value.isBlank()) return defaultValue;
        return converter.apply(value);
    }

    @Override
    public Boolean hasParam(String param) {
        return args.containsOption(param);
    }
}
