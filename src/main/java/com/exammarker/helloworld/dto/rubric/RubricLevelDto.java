package com.exammarker.helloworld.dto.rubric;

import java.util.List;

public record RubricLevelDto(
        Integer levelNumber,
        String label,
        MarkRangeDto markRange,
        String descriptor,
        List<String> characteristics,
        List<String> evidenceKeywords
) {}