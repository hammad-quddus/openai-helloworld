package com.exammarker.helloworld.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.exammarker.helloworld.dto.ExamEvaluationDto;
import com.exammarker.helloworld.dto.rubric.RubrickDto;
import com.exammarker.helloworld.dto.solution.SolutionDto;
import com.exammarker.helloworld.service.ExamEvaluationService;
import com.exammarker.helloworld.service.PdfAssemblyService;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/exam")
public class ExamController {

    private static final Logger log =
            LoggerFactory.getLogger(PdfAssemblyService.class);
    
    
    private final ExamEvaluationService evaluationService;
    private final PdfAssemblyService pdfAssemblyservice;


    public ExamController(ExamEvaluationService service, PdfAssemblyService pdfAssemblyservice) {
        this.evaluationService = service;
        this.pdfAssemblyservice = pdfAssemblyservice;
    }

    @PostMapping(value = "/transcribesolution", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SolutionDto transcribeSolution(

    		@RequestPart(value = "solution", required = true) List<MultipartFile> solutionImages

    ) throws Exception {

    	log.info("endpoint: transcribesolution...");
 
    	return evaluationService.transcribeSolutions(solutionImages);

    }
   
    
    @PostMapping(value = "/transcriberubric", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RubrickDto transcribeRubric(

    		@RequestPart(value = "rubric", required = true) List<MultipartFile> rubricImages

    ) throws Exception {

    	log.info("endpoint: transcriberubric...");
 
    	return evaluationService.transcribeRubrick(rubricImages);

    }
    
    @PostMapping(value = "/evaluate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ExamEvaluationDto evaluate(
            @RequestPart("paperImages") List<MultipartFile> paperImages,
            @RequestPart("rubricImages") List<MultipartFile> rubricImages,
            @RequestPart("solutionImages") List<MultipartFile> solutionImages
    ) throws Exception {
    	
    	log.info("endpoint: evaluat...");
    	log.info("total paperImages: " + paperImages.size());
    	log.info("total rubricImages: " + rubricImages.size());
    	log.info("total solutionImages: " + solutionImages.size());
    	
    	
    	return evaluationService.evaluate(paperImages, rubricImages, solutionImages);
   }
}