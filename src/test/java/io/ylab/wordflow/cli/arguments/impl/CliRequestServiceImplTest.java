package io.ylab.wordflow.cli.arguments.impl;

import io.ylab.wordflow.cli.properties.IPropertyService;
import io.ylab.wordflow.configuration.WordFlowConfiguration;
import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.enums.ProcessingMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Тесты для {@link CliRequestServiceImpl}.
 * Проверяют парсинг аргументов командной строки
 * с использованием {@link IPropertyService} и {@link WordFlowConfiguration}.
 */
@ExtendWith(MockitoExtension.class)
class CliRequestServiceImplTest {

    @Mock
    private IPropertyService propertyService;

    @Mock
    private WordFlowConfiguration config;

    @InjectMocks
    private CliRequestServiceImpl requestService;

    /**
     * Устанавливает значения по умолчанию для конфигурации.
     * По умолчанию mode = MULTI, threads = 2 (как в application.yml).
     */
    @BeforeEach
    void setUp() {
        when(config.mode()).thenReturn(ProcessingMode.MULTI);
        when(config.threads()).thenReturn(2);
    }

    /**
     * Проверяет успешный парсинг всех параметров (обязательных и опциональных)
     * в многопоточном режиме.
     */
    @Test
    void parse_whenValidArgsMultiThreaded_shouldReturnRequestDto() {
        // given
        when(propertyService.getRequiredString("dir")).thenReturn("./texts");
        when(propertyService.getRequiredInt("min-length")).thenReturn(5);
        when(propertyService.getRequiredInt("top")).thenReturn(20);
        when(propertyService.getString("output", null)).thenReturn("./result.json");
        when(propertyService.getString("stopwords", null)).thenReturn("./stop.txt");
        when(propertyService.getEnum("mode", config.mode(), ProcessingMode.class)).thenReturn(ProcessingMode.MULTI);
        when(propertyService.getInt("threads", config.threads())).thenReturn(4);

        RequestDto request = requestService.parse();

        assertThat(request).isNotNull();
        assertThat(request.directory()).isEqualTo("./texts");
        assertThat(request.minLength()).isEqualTo(5);
        assertThat(request.top()).isEqualTo(20);
        assertThat(request.outputFile()).isEqualTo("./result.json");
        assertThat(request.stopWordsFile()).isEqualTo("./stop.txt");
        assertThat(request.mode()).isEqualTo(ProcessingMode.MULTI);
        assertThat(request.threads()).isEqualTo(4);

        verify(propertyService).getRequiredString("dir");
        verify(propertyService).getRequiredInt("min-length");
        verify(propertyService).getRequiredInt("top");
        verify(propertyService).getString("output", null);
        verify(propertyService).getString("stopwords", null);
        verify(propertyService).getEnum("mode", config.mode(), ProcessingMode.class);
        verify(propertyService).getInt("threads", config.threads());
    }

    /**
     * Проверяет, что в однопоточном режиме ({@code mode = SINGLE}) количество потоков
     * принудительно устанавливается в {@code 1}, независимо от значения {@code threads}.
     */
    @Test
    void parse_whenSingleThreaded_shouldForceThreadsToOne() {
        when(propertyService.getRequiredString("dir")).thenReturn("./texts");
        when(propertyService.getRequiredInt("min-length")).thenReturn(3);
        when(propertyService.getRequiredInt("top")).thenReturn(10);
        when(propertyService.getString("output", null)).thenReturn(null);
        when(propertyService.getString("stopwords", null)).thenReturn(null);
        when(propertyService.getEnum("mode", config.mode(), ProcessingMode.class)).thenReturn(ProcessingMode.SINGLE);

        RequestDto request = requestService.parse();

        assertThat(request.mode()).isEqualTo(ProcessingMode.SINGLE);
        assertThat(request.threads()).isEqualTo(1);
        verify(propertyService, never()).getInt("threads", config.threads());
    }

    /**
     * Проверяет, что опциональные параметры (output, stopwords) могут отсутствовать,
     * и при этом используются значения по умолчанию {@code null}.
     */
    @Test
    void parse_whenOptionalParamsMissing_shouldSetNull() {
        // given
        when(propertyService.getRequiredString("dir")).thenReturn("./texts");
        when(propertyService.getRequiredInt("min-length")).thenReturn(3);
        when(propertyService.getRequiredInt("top")).thenReturn(10);
        when(propertyService.getString("output", null)).thenReturn(null);
        when(propertyService.getString("stopwords", null)).thenReturn(null);
        when(propertyService.getEnum("mode", config.mode(), ProcessingMode.class)).thenReturn(ProcessingMode.MULTI);
        when(propertyService.getInt("threads", config.threads())).thenReturn(2);

        RequestDto request = requestService.parse();

        assertThat(request.outputFile()).isNull();
        assertThat(request.stopWordsFile()).isNull();
        assertThat(request.mode()).isEqualTo(ProcessingMode.MULTI);
        assertThat(request.threads()).isEqualTo(2);
    }
}