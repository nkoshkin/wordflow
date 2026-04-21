package io.ylab.wordflow.mapper;

import io.ylab.wordflow.dto.ErrorDto;
import io.ylab.wordflow.dto.InfoDto;
import io.ylab.wordflow.dto.ResponseDto;
import io.ylab.wordflow.dto.WordCountDto;
import io.ylab.wordflow.entity.AnalysisEntity;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования сущностей в DTO.
 * Используется в контроллерах для формирования ответов клиенту.
 * <p>
 * Преобразует {@link AnalysisEntity} в {@link ResponseDto}
 * </p>
 */
@Component
public class AnalysisDtoMapper {

    public ResponseDto toResponseDto(AnalysisEntity entity) {
        InfoDto info = new InfoDto(
                entity.getDirectory(),
                entity.getMinLength(),
                entity.getTop(),
                entity.getMode(),
                entity.getThreads(),
                entity.getProcessedFiles(),
                entity.getExecutionTimeMs()
        );

        var words = entity.getWords().stream()
                .map(w -> new WordCountDto(w.getWord(), w.getCount()))
                .toList();

        var errors = entity.getErrors().stream()
                .map(e -> new ErrorDto(e.getFile(), e.getMessage()))
                .toList();

        return new ResponseDto(info, words, errors);
    }
}
