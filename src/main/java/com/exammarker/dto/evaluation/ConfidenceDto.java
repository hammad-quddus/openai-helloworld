package com.exammarker.dto.evaluation;

// meta layer
public record ConfidenceDto(
    Double transcriptionConfidence,
    Double gradingConfidence
) {}