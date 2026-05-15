package com.exammarker.helloworld.dto.studentpaper;
import java.util.List;

public record StudentPaperDto(
        String subject,
        String studentId,
        List<QuestionAnswerDto> questions
) {}