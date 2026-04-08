package io.ylab.wordflow.cli.properties.impl;

import io.ylab.wordflow.cli.properties.IPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
@Profile("cli")
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
    public <T extends Enum<T>> T getEnum(String param, T defaultValue, Class<T> enumClass) {
        String value = getString(param, null);
        if (value == null || value.isBlank()){
            return defaultValue;
        }
        return Enum.valueOf(enumClass, value.toUpperCase());
    }

    @Override
    public Boolean hasParam(String param) {
        return args.containsOption(param);
    }
}
