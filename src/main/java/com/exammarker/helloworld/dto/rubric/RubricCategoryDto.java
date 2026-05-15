package com.exammarker.helloworld.dto.rubric;

import java.util.List;

public record RubricCategoryDto(
        String rubricCategoryId,
        String assessmentObjective,
        Integer maxMarks,
        String appliesTo,
        List<RubricLevelDto> levelScale
) {}