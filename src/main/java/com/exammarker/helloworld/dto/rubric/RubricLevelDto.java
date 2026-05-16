package com.exammarker.helloworld.dto.rubric;

import java.util.List;

public record RubricLevelDto(
        String levelId,
        Integer levelNumber,
        MarkRangeDto markRange,
        String descriptor,
        List<String> characteristics,
        List<String> evidenceKeywords
) {}