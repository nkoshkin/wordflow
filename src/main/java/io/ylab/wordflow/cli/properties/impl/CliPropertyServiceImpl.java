package io.ylab.wordflow.cli.properties.impl;

import io.ylab.wordflow.cli.properties.IPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

/**
 * Реализация {@link IPropertyService} для чтения параметров командной строки.
 * Использует {@link ApplicationArguments} для доступа к аргументам, переданным при запуске.
 *
 * <p>Поддерживает формат {@code --key=value} (стандартный парсинг Spring Boot).</p>
 *
 * <p>Пример использования:
 * <pre>
 * String dir = propertyService.getRequiredString("dir");
 * int threads = propertyService.getInt("threads", 2);
 * ProcessingMode mode = propertyService.getEnum("mode", ProcessingMode.MULTI, ProcessingMode.class);
 * </pre>
 * </p>
 *
 * @see IPropertyService
 */
@Service
@Profile("cli")
public class CliPropertyServiceImpl implements IPropertyService {
    @Autowired
    ApplicationArguments args;

    /**
     * {@inheritDoc}
     *
     * <p>Реализация извлекает значение параметра из {@code args.getOptionValues(param)}.
     * Если параметр не указан или пуст, возвращается {@code defaultValue}.
     * В случае ошибки преобразования также возвращается {@code defaultValue} и логируется предупреждение.</p>
     *
     * @param param        имя параметра
     * @param defaultValue значение по умолчанию
     * @param converter    функция преобразования
     * @param <T>          целевой тип
     * @return сконвертированное значение или {@code defaultValue}
     */
    @Override
    public <T> T getValue(String param, T defaultValue, Function<String, T> converter) {
        List<String> values = args.getOptionValues(param);
        String value = (values != null && !values.isEmpty())? values.getFirst() : null;
        if (value == null || value.isBlank()) return defaultValue;
        return converter.apply(value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Реализация преобразует строковое значение в константу перечисления,
     * приводя строку к верхнему регистру. Если значение не соответствует ни одной константе,
     * возвращается {@code defaultValue} и логируется предупреждение.</p>
     *
     * @param param        имя параметра
     * @param defaultValue значение по умолчанию
     * @param enumClass    класс перечисления
     * @param <T>          тип перечисления
     * @return значение перечисления или {@code defaultValue}
     */
    @Override
    public <T extends Enum<T>> T getEnum(String param, T defaultValue, Class<T> enumClass) {
        String value = getString(param, null);
        if (value == null || value.isBlank()){
            return defaultValue;
        }
        return Enum.valueOf(enumClass, value.toUpperCase());
    }

    /**
     * {@inheritDoc}
     *
     * @param param имя параметра
     * @return {@code true}, если параметр присутствует (даже без значения), иначе {@code false}
     */
    @Override
    public Boolean hasParam(String param) {
        return args.containsOption(param);
    }
}
