package com.exammarker.helloworld.dto;

// meta layer
public record ConfidenceDto(
    Double transcriptionConfidence,
    Double gradingConfidence
) {}