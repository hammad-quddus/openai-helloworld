package com.exammarker.helloworld.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.exammarker.helloworld.dto.ExamEvaluationDto;
import com.exammarker.helloworld.service.ExamEvaluationService;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/exam")
public class ExamController {

	
    private final ExamEvaluationService service;


    public ExamController(ExamEvaluationService service) {
        this.service = service;
    }

    @PostMapping(value = "/evaluate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ExamEvaluationDto evaluate(
            @RequestPart("paper") MultipartFile paper,
            @RequestPart("rubric") MultipartFile rubric,
            @RequestPart("solutions") MultipartFile solutions
    ) throws Exception {
    	
    	return service.evaluate(paper, rubric, solutions);
   }
}