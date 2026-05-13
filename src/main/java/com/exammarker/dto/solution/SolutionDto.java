package com.exammarker.dto.solution;
public record SolutionDto(
    String fileHash,
    String extractedContent
) {}