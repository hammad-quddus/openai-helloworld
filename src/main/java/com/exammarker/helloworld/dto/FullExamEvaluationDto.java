package com.exammarker.helloworld.dto;

import java.util.List;

public record FullExamEvaluationDto(
    List<QuestionEvaluationDto> questionEvaluations
) {}