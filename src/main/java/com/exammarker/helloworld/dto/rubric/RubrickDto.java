package com.exammarker.helloworld.dto.rubric;
import java.util.List;

public record RubrickDto(
        String rubricId,
        String subject,
        List<RubricCategoryDto> categories
) {}