package com.exammarker.helloworld.dto.rubric;
import java.util.List;

import com.exammarker.helloworld.dto.MarkRangeDto;

public record LevelScaleDto(
        Integer levelNumber,
        String label,
        MarkRangeDto markRange,
        String descriptor,
        List<String> characteristics,
        List<String> evidenceKeywords
) {}