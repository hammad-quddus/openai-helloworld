package com.exammarker.helloworld.dto.solution;

import java.util.List;

public record QuestionSolutionDto(
        String questionId,
        String modelAnswer,
        List<String> keyPoints,
        List<String> acceptableAlternativePoints,
        List<String> evidenceReferences
) {}