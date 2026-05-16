package com.exammarker.helloworld.dto.studentpaper;
public record QuestionAnswerDto(
        String questionId,
        String rawQuestionLabel,
        String subQuestionLabel,
        String questionText,
        String answerText
) {}