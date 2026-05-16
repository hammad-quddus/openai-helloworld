package com.exammarker.helloworld.dto.rubric;
public record QuestionMappingDto(
        String questionId,
        String rubricCategoryId,
        Integer maxMarks
) {}