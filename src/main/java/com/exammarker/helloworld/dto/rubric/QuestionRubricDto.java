package com.exammarker.helloworld.dto.rubric;
import java.util.List;

public record QuestionRubricDto(
        String questionId,
        Integer maxMarks,
        List<LevelScaleDto> levelScale
) {}