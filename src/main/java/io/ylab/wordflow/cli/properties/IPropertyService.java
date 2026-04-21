package io.ylab.wordflow.cli.properties;

import io.ylab.wordflow.cli.properties.impl.CliPropertyServiceImpl;
import org.springframework.boot.ApplicationArguments;

import java.util.function.Function;
/**
 * Сервис для получения значений параметров.
 * Предоставляет унифицированный доступ к параметрам с возможностью преобразования типов и задания значений по умолчанию.
 *
 * <p>Методы разделены на опциональные (возвращают значение по умолчанию, если параметр отсутствует)
 * и обязательные (выбрасывают исключение).</p>
 *
 * <p>Основной метод – {@link #getValue(String, Object, Function)} – позволяет получить значение произвольного типа
 * с помощью переданной функции-конвертера.</p>
 *
 * <p>Текущая реализация для CLI использует {@link ApplicationArguments} и поддерживает формат {@code --key=value}.</p>
 *
 * @see CliPropertyServiceImpl
 */
public interface IPropertyService {

    /**
     * Универсальный метод получения параметра с преобразованием в требуемый тип.
     *
     * @param param имя параметра (без дефисов, например "dir")
     * @param defaultValue значение по умолчанию, возвращаемое при отсутствии параметра или ошибке преобразования
     * @param converter функция для преобразования строки в целевой тип
     * @param <T> целевой тип
     * @return сконвертированное значение или {@code defaultValue}, если параметр отсутствует или конвертация не удалась
     */
    <T> T getValue(String param, T defaultValue, Function<String, T> converter);

    /**
     * Возвращает обязательный параметр, выбрасывая исключение при его отсутствии.
     *
     * @param param имя параметра
     * @param converter функция для преобразования строки в целевой тип
     * @param <T> целевой тип
     * @return сконвертированное значение
     * @throws IllegalArgumentException если параметр отсутствует
     */
    default <T> T getRequaredValue(String param, Function<String, T> converter) {
        T value = getValue(param, null, converter);
        if (value == null) throw new IllegalArgumentException("Missing required parameter: --" + param);
        return value;
    }

    /**
     * Возвращает строковый параметр или значение по умолчанию.
     *
     * @param param имя параметра
     * @param defaultValue значение по умолчанию
     * @return строковое значение или {@code defaultValue}
     */
    default String getString(String param, String defaultValue){
        return getValue(param, defaultValue, Function.identity());
    }

    /**
     * Возвращает обязательный строковый параметр.
     *
     * @param param имя параметра
     * @return строковое значение
     * @throws IllegalArgumentException если параметр отсутствует
     */
    default String getRequiredString(String param){
        return getRequaredValue(param, Function.identity());
    }

    /**
     * Возвращает целочисленный параметр или значение по умолчанию.
     *
     * @param param имя параметра
     * @param defaultValue значение по умолчанию
     * @return целое число или {@code defaultValue}
     */
    default Integer getInt(String param, Integer defaultValue){
        return getValue(param, defaultValue, Integer::parseInt);
    }

    /**
     * Возвращает обязательный целочисленный параметр.
     *
     * @param param имя параметра
     * @return целое число
     * @throws IllegalArgumentException если параметр отсутствует или не является целым числом
     */
    default Integer getRequiredInt(String param){
        return getRequaredValue(param, Integer::parseInt);
    }

    /**
     * Возвращает значение перечисления (enum) или значение по умолчанию.
     *
     * @param param имя параметра
     * @param defaultValue значение по умолчанию (не {@code null})
     * @param enumClass класс перечисления
     * @param <T> тип перечисления
     * @return значение перечисления или {@code defaultValue}
     */
    <T extends Enum<T>> T getEnum(String param, T defaultValue, Class<T> enumClass);

    /**
     * Проверяет наличие параметра в источнике.
     *
     * @param param имя параметра
     * @return {@code true}, если параметр присутствует (даже без значения), иначе {@code false}
     */
    Boolean hasParam(String param);

}
