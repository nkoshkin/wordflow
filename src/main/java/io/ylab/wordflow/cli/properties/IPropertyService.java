package io.ylab.wordflow.cli.properties;

import java.util.function.Function;

public interface IPropertyService {

    <T> T getValue(String param, T defaultValue, Function<String, T> converter);

    default <T> T getRequaredValue(String param, Function<String, T> converter) {
        T value = getValue(param, null, converter);
        if (value == null) throw new IllegalArgumentException("Missing required parameter: --" + param);
        return value;
    }

    default String getString(String param, String defaultValue){
        return getValue(param, defaultValue, Function.identity());
    }

    default String getRequiredString(String param){
        return getRequaredValue(param, Function.identity());
    }

    default Integer getInt(String param, Integer defaultValue){
        return getValue(param, defaultValue, Integer::parseInt);
    }

    default Integer getRequiredInt(String param){
        return getRequaredValue(param, Integer::parseInt);
    }

    <T extends Enum<T>> T getEnum(String param, T defaultValue, Class<T> enumClass);

    Boolean hasParam(String param);

}
