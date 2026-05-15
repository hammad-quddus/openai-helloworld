package com.exammarker.helloworld.dto.rubric;

import com.exammarker.helloworld.dto.BandDto;

// judgement layer
public record RubricReferenceDto(

    BandDto band,

    String descriptor

) {}