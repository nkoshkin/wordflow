package io.ylab.wordflow.cli.properties.impl;

import io.ylab.wordflow.cli.properties.IPropertyService;
import io.ylab.wordflow.enums.ProcessingMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Тесты для {@link CliPropertyServiceImpl}.
 * Проверяют чтение параметров командной строки через {@link ApplicationArguments}.
 *
 */
@ExtendWith(MockitoExtension.class)
class CliPropertyServiceImplTest {

    @Mock
    private ApplicationArguments args;

    private IPropertyService propertyService;

    @BeforeEach
    void setUp() {
        propertyService = new CliPropertyServiceImpl();
        ((CliPropertyServiceImpl) propertyService).args = args;
    }

    /**
     * Проверяет, что {@link CliPropertyServiceImpl#getValue(String, Object, Function)}
     * возвращает значение, если параметр присутствует и конвертация успешна.
     */
    @Test
    void getValue_whenParamExists_shouldReturnConvertedValue() {
        when(args.getOptionValues("param")).thenReturn(List.of("123"));
        Integer result = propertyService.getValue("param", 0, Integer::parseInt);
        assertThat(result).isEqualTo(123);
    }

    /**
     * Проверяет, что при отсутствии параметра возвращается значение по умолчанию.
     */
    @Test
    void getValue_whenParamMissing_shouldReturnDefault() {
        when(args.getOptionValues("missing")).thenReturn(null);
        String result = propertyService.getValue("missing", "default", Function.identity());
        assertThat(result).isEqualTo("default");
    }

    /**
     * Проверяет, что при пустом значении параметра возвращается значение по умолчанию.
     */
    @Test
    void getValue_whenParamEmpty_shouldReturnDefault() {
        when(args.getOptionValues("empty")).thenReturn(List.of(""));
        String result = propertyService.getValue("empty", "default", Function.identity());
        assertThat(result).isEqualTo("default");
    }

    /**
     * Проверяет, что при ошибке конвертации (например, нечисловое значение для Integer)
     * выбрасывается {@link NumberFormatException}.
     */
    @Test
    void getValue_whenConversionFails_shouldThrowException() {
        when(args.getOptionValues("invalid")).thenReturn(List.of("not_a_number"));
        assertThatThrownBy(() -> propertyService.getValue("invalid", 42, Integer::parseInt))
                .isInstanceOf(NumberFormatException.class);
    }

    /**
     * Проверяет, что {@link CliPropertyServiceImpl#getEnum(String, Enum, Class)}
     * корректно преобразует строку в enum.
     */
    @Test
    void getEnum_whenValidValue_shouldReturnEnum() {
        when(args.getOptionValues("mode")).thenReturn(List.of("single"));
        ProcessingMode result = propertyService.getEnum("mode", ProcessingMode.MULTI, ProcessingMode.class);
        assertThat(result).isEqualTo(ProcessingMode.SINGLE);
    }

    /**
     * Проверяет, что при неверном значении (не соответствующем ни одной константе enum)
     * выбрасывается {@link IllegalArgumentException}.
     */
    @Test
    void getEnum_whenInvalidValue_shouldThrowException() {
        when(args.getOptionValues("mode")).thenReturn(List.of("invalid_mode"));
        assertThatThrownBy(() -> propertyService.getEnum("mode", ProcessingMode.MULTI, ProcessingMode.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Проверяет, что {@link CliPropertyServiceImpl#hasParam(String)} возвращает {@code true},
     * если параметр присутствует.
     */
    @Test
    void hasParam_whenParamExists_shouldReturnTrue() {
        when(args.containsOption("dir")).thenReturn(true);
        boolean result = propertyService.hasParam("dir");
        assertThat(result).isTrue();
    }

    /**
     * Проверяет, что {@link CliPropertyServiceImpl#hasParam(String)} возвращает {@code false},
     * если параметр отсутствует.
     */
    @Test
    void hasParam_whenParamMissing_shouldReturnFalse() {
        when(args.containsOption("missing")).thenReturn(false);
        boolean result = propertyService.hasParam("missing");
        assertThat(result).isFalse();
    }
}