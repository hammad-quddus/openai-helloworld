package com.exammarker.helloworld.dto.rubric;
import java.util.List;

public record RubricDto(
        String rubricId,
        String subject,
        List<RubricCategoryDto> rubricCategories,
        List<QuestionMappingDto> questionMappings
) {}