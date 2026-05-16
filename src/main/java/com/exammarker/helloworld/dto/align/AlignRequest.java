package com.exammarker.helloworld.dto.align;

import com.exammarker.helloworld.dto.solution.SolutionDto;
import com.exammarker.helloworld.dto.studentpaper.StudentPaperDto;

public record AlignRequest(
        StudentPaperDto studentPaper,
        SolutionDto solution
) {}