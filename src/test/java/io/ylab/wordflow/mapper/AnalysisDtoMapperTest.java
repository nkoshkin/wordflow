package io.ylab.wordflow.mapper;

import io.ylab.wordflow.dto.ResponseDto;
import io.ylab.wordflow.entity.AnalysisEntity;
import io.ylab.wordflow.entity.ErrorEntity;
import io.ylab.wordflow.entity.WordCountEntity;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для {@link AnalysisDtoMapper}.
 * Проверяют преобразование сущностей в DTO.
 */
class AnalysisDtoMapperTest {

    private final AnalysisDtoMapper mapper = new AnalysisDtoMapper();

    /**
     * Проверяет, что {@link AnalysisDtoMapper#toResponseDto(AnalysisEntity)} корректно
     * преобразует сущность в {@link ResponseDto}, включая метаинформацию, слова и ошибки.
     */
    @Test
    void toResponseDto_shouldConvertEntityToDto() {
        UUID id = UUID.randomUUID();
        AnalysisEntity entity = AnalysisEntity.builder()
                .id(id)
                .directory("./dir")
                .minLength(3)
                .top(10)
                .mode("multi")
                .threads(4)
                .processedFiles(5)
                .executionTimeMs(123L)
                .words(List.of(
                        WordCountEntity.builder().word("word1").count(10).build(),
                        WordCountEntity.builder().word("word2").count(5).build()
                ))
                .errors(List.of(
                        ErrorEntity.builder().file("error.txt").message("Access denied").build()
                ))
                .build();

        ResponseDto response = mapper.toResponseDto(entity);

        assertThat(response.infoDto().directory()).isEqualTo("./dir");
        assertThat(response.infoDto().minLength()).isEqualTo(3);
        assertThat(response.infoDto().top()).isEqualTo(10);
        assertThat(response.infoDto().mode()).isEqualTo("multi");
        assertThat(response.infoDto().threads()).isEqualTo(4);
        assertThat(response.infoDto().processedFiles()).isEqualTo(5);
        assertThat(response.infoDto().executionTimeMs()).isEqualTo(123L);
        assertThat(response.words()).hasSize(2);
        assertThat(response.words().getFirst().word()).isEqualTo("word1");
        assertThat(response.words().getFirst().count()).isEqualTo(10);
        assertThat(response.errors()).hasSize(1);
        assertThat(response.errors().getFirst().file()).isEqualTo("error.txt");
        assertThat(response.errors().getFirst().message()).isEqualTo("Access denied");
    }

    /**
     * Проверяет преобразование сущности с пустыми коллекциями слов и ошибок.
     */
    @Test
    void toResponseDto_whenEmptyLists_shouldReturnEmptyDtos() {
        AnalysisEntity entity = AnalysisEntity.builder()
                .directory("./dir")
                .minLength(3)
                .top(10)
                .mode("multi")
                .threads(4)
                .processedFiles(0)
                .executionTimeMs(0L)
                .words(List.of())
                .errors(List.of())
                .build();

        ResponseDto response = mapper.toResponseDto(entity);

        assertThat(response.words()).isEmpty();
        assertThat(response.errors()).isEmpty();
    }
}