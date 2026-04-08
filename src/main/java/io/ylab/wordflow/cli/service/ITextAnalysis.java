package io.ylab.wordflow.cli.service;

import io.ylab.wordflow.dto.RequestDto;
import io.ylab.wordflow.dto.ResponseDto;


public interface ITextAnalysis {

    ResponseDto analyze(RequestDto requestDto);

}
