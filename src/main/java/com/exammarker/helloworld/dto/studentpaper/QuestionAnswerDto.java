package com.exammarker.helloworld.dto.studentpaper;
public record QuestionAnswerDto(
        String questionId,
        String rawQuestionLabel,
        String questionText,
        String answerText
) {}