package com.exammarker.helloworld.dto.solution;
import java.util.List;

public record SolutionDto(
        String subject,
        List<QuestionSolutionDto> questions
) {
}