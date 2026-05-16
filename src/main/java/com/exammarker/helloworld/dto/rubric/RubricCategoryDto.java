package com.exammarker.helloworld.dto.rubric;

import java.util.List;

public record RubricCategoryDto(
        String rubricCategoryId,
        String assessmentObjective,
        String description,
        String scoringRule,
        List<RubricLevelDto> levels
) {}