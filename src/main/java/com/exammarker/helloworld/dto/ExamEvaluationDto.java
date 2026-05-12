package com.exammarker.helloworld.dto;

import java.util.List;


// raw output layer
public record ExamEvaluationDto(

    Integer questionNumber,
    String questionText,

    Integer maxMarks,
    Integer marksAwarded,

    String studentSolutionTranscription,

    List<String> officialSolutionKeyPoints,

    EvaluationDto evaluation,

    List<String> strengths,
    List<String> improvements,
    List<String> factualErrors,
    List<String> teacherComments,

    RubricReferenceDto rubricReference,

    ConfidenceDto confidence,

    Boolean requiresHumanReview,

    List<EvidenceDto> evidence

) {}