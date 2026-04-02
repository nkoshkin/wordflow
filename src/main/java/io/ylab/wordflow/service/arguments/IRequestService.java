package io.ylab.wordflow.service.arguments;

import io.ylab.wordflow.dto.RequestDto;

import java.util.Optional;

public interface IRequestService {

    Optional<RequestDto> parse();

}
