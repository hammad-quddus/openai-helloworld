package com.exammarker.helloworld.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.exammarker.helloworld.dto.ExamEvaluationDto;
import com.exammarker.helloworld.service.ExamEvaluationService;
import com.exammarker.helloworld.service.PdfAssemblyService;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/exam")
public class ExamController {

	
    private final ExamEvaluationService evaluationService;
    private final PdfAssemblyService pdfAssemblyservice;


    public ExamController(ExamEvaluationService service, PdfAssemblyService pdfAssemblyservice) {
        this.evaluationService = service;
        this.pdfAssemblyservice = pdfAssemblyservice;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadExam(
    		@RequestPart("paper") List<MultipartFile> paperImages,
    		@RequestPart(value = "rubric", required = false) List<MultipartFile> rubricImages,
    		@RequestPart(value = "solutions", required = false) List<MultipartFile> solutionImages
    ) {

        // process images
    	System.out.println(paperImages.size());
    	try {
			pdfAssemblyservice.imagesToPdf(paperImages);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
//        return ResponseEntity.ok().build();
    }
    
    @PostMapping(value = "/evaluate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ExamEvaluationDto evaluate(
            @RequestPart("paperImages") List<MultipartFile> paperImages,
            @RequestPart("rubricImages") List<MultipartFile> rubricImages,
            @RequestPart("solutionImages") List<MultipartFile> solutionsImages
    ) throws Exception {
    	System.out.println("evaluat...");
    	return evaluationService.evaluate(paperImages, rubricImages, solutionsImages);
   }
}