package com.exammarker.helloworld.dto;

import java.util.List;


// raw output layer
public record ExamEvaluationDto(

	String studentName,	
    Integer questionNumber,
    String questionText,

    Integer maxMarks,
    Integer marksAwarded,

    String studentSolutionTranscription,

    List<String> officialSolutionKeyPoints,

    EvaluationDto evaluation,
    String evaluationSummary,

    List<String> strengths,
    List<String> improvements,
    List<String> factualErrors,
    List<String> teacherComments,
    List<String> coverageGaps,
	
    RubricReferenceDto rubricReference,

    ConfidenceDto confidence,

    Boolean requiresHumanReview

//    List<EvidenceDto> evidence

) {}