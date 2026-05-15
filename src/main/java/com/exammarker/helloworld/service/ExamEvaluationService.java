package com.exammarker.helloworld.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import com.exammarker.helloworld.dto.ExamEvaluationDto;
import com.exammarker.helloworld.dto.rubric.RubrickDto;
import com.exammarker.helloworld.dto.solution.SolutionDto;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

@Service
public class ExamEvaluationService {

	private static final Logger log = LoggerFactory.getLogger(PdfAssemblyService.class);

	private final OpenAiChatModel chatModel;

	private final PdfAssemblyService pdfAssemblyService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public ExamEvaluationService(OpenAiChatModel chatModel, PdfAssemblyService pdfAssemblyService) {
		this.chatModel = chatModel;
		this.pdfAssemblyService = pdfAssemblyService;
	}

	public ExamEvaluationDto evaluate(List<MultipartFile> paperImages, List<MultipartFile> rubricImages,
			List<MultipartFile> solutionImages) throws Exception {

		byte[] paperPdfBytes = pdfAssemblyService.imagesToPdf(paperImages);
		byte[] rubricPdfBytes = pdfAssemblyService.imagesToPdf(rubricImages);
		byte[] solutionPdfBytes = pdfAssemblyService.imagesToPdf(solutionImages);

		Resource studentWorkPdf = new ByteArrayResource(paperPdfBytes);

		Resource solutionsPdf = new ByteArrayResource(solutionPdfBytes);

		Resource rubricPdf = new ByteArrayResource(rubricPdfBytes);

		SystemMessage systemMessage = new SystemMessage(
				"""
						        You are an experienced 9th-grade Islamic Studies teacher.

						        Read ALL attached files carefully.

						Tasks:
						1. Read the rubric
						2. Read the exam solutions
						3. Read the student's handwritten paper
						4. Transcribe the student answers
						5. Compare against solutions
						6. Assign marks out of 10
						7. Identify key expected points that are missing or insufficiently addressed in the student's answer (coverage gaps)

						Rules:
						- Never invent student answers
						- If handwriting is unreadable, explicitly say so
						- Base grading on the supplied rubric
						- Be strict but fair
						- Coverage gaps must strictly come from rubric/official solutions (do not hallucinate new expectations)
						- Return ONLY valid JSON
						- Do not return markdown
						- Do not wrap JSON in triple backticks
						- The uploaded pages of the student paper may be out of order; use content continuity and context to infer the correct sequence where necessary before grading.

						JSON schema:
						{
						  "studentName": string | null,
						  "questionNumber": integer | null,
						  "questionText": string,
						  "maxMarks": integer,
						  "marksAwarded": integer,
						  "studentSolutionTranscription": string,

						  "officialSolutionKeyPoints": [
						    string
						  ],

						  "coverageGaps": [
						    string
						  ],

						  "evaluation": {
						    "accuracy": [
						      string
						    ],
						    "coverage": [
						      string
						    ],
						    "useOfResources": [
						      string
						    ],
						    "structure": [
						      string
						    ],
						    "relevance": [
						      string
						    ]
						  },

						  "evaluationSummary": string,

						  "strengths": [
						    string
						  ],

						  "improvements": [
						    string
						  ],

						  "factualErrors": [
						    string
						  ],

						  "teacherComments": [
						    string
						  ],

						  "rubricReference": {
						    "band": {
						      "min": integer,
						      "max": integer
						    },
						    "descriptor": string
						  },

						  "confidence": {
						    "transcriptionConfidence": number,
						    "gradingConfidence": number
						  },

						  "requiresHumanReview": boolean
						}
						""");

		UserMessage rubricMessage = UserMessage.builder().text("This is the grading rubric.")
				.media(new Media(MimeTypeUtils.parseMimeType("application/pdf"), rubricPdf)).build();

		UserMessage solutionsMessage = UserMessage.builder().text("These are the official exam solutions.")
				.media(new Media(MimeTypeUtils.parseMimeType("application/pdf"), solutionsPdf)).build();

		UserMessage studentMessage = UserMessage.builder().text("""
				This is the student's handwritten exam paper.

				Please:
				- transcribe the student's answer carefully
				- identify unclear or unreadable handwriting
				- compare the answer against the supplied marking scheme
				- evaluate the answer using the rubric
				- extract supporting evidence directly from the student's writing
				- assign marks fairly and accurately
				- return ONLY valid JSON matching the required schema
				""").media(new Media(MimeTypeUtils.parseMimeType("application/pdf"), studentWorkPdf)).build();

		Prompt prompt = new Prompt(List.of(systemMessage, rubricMessage, solutionsMessage, studentMessage));

		ChatResponse response;
		try {
			response = chatModel.call(prompt);
		} catch (Exception e) {
			throw new RuntimeException("AI grading failed", e);
		}

		var raw = response.getResult().getOutput().getText();

		log.info("====== Response from ai model: ========");
		log.info(raw);

		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		ExamEvaluationDto dto = objectMapper.readValue(raw, ExamEvaluationDto.class);

		validate(dto);

		return dto;
	}

	////////

	public RubrickDto transcribeRubrick(List<MultipartFile> rubricImages) throws Exception {

		byte[] rubricPdfBytes = pdfAssemblyService.imagesToPdf(rubricImages);
		Resource rubricPdf = new ByteArrayResource(rubricPdfBytes);

		return transcribeRubrick(rubricPdf);

	}

	public RubrickDto transcribeRubrick(Resource rubricPdf) throws Exception {

		SystemMessage systemMessage = new SystemMessage("""
				You are a rubric transcription engine.

				TASK:
				Extract and normalize the rubric from the attached PDF into the provided JSON schema.

				STRICT RULES:
				- Only extract information explicitly present in the rubric.
				- Do NOT infer missing mark schemes or grading logic.
				- Do NOT improve, rewrite, or reinterpret content.
				- Preserve original meaning as closely as possible.

				If information is unclear, missing, or unreadable:
				- set the field to null
				- add explanation to "warnings"

				Ignore:
				- formatting issues
				- layout artifacts
				- OCR noise
				- repeated or misaligned text

				The goal is structural transcription, not reasoning.

				OUTPUT:
				Must strictly follow JSON schema. No extra fields. No commentary.

				JSON Schema:

				{
				  "rubricId": "string",
				  "subject": "string",
				
				  "questions": [
				    {
				      "questionId": "string",
				      "maxMarks": "number",
				
				      "levelScale": [
				        {
				          "levelNumber": 0,
				          "label": "string",
				          "markRange": {
				            "min": 0,
				            "max": 0
				          },
				          "descriptor": "string",
				          "characteristics": ["string"],
				          "evidenceKeywords": ["string"]
				        }
				      ]
				    }
				  ]
				}
																""");

		UserMessage rubricMessage = UserMessage.builder().text("This is the grading rubric.. pls..")
				.media(new Media(MimeTypeUtils.parseMimeType("application/pdf"), rubricPdf)).build();

		Prompt prompt = new Prompt(List.of(systemMessage, rubricMessage));

		ChatResponse response;
		try {
			response = chatModel.call(prompt);
		} catch (Exception e) {
			throw new RuntimeException("AI parsing failed", e);
		}

		var raw = response.getResult().getOutput().getText();

		log.info("====== Response from ai model for rubric transcription: ========");
		log.info(raw);

		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

//		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

		RubrickDto dto = objectMapper.readValue(raw, RubrickDto.class);

		return dto;
	}

	///
	/// 
	/// 
	/// 
	///
	
	public SolutionDto transcribeSolutions(List<MultipartFile> solutionsImages) throws Exception {

		byte[] solutionPdfBytes = pdfAssemblyService.imagesToPdf(solutionsImages);
		Resource solutionPdf = new ByteArrayResource(solutionPdfBytes);

		return transcribeSolutions(solutionPdf);

	}

	
	
	public SolutionDto transcribeSolutions(Resource solutionPdf) throws Exception {

	SystemMessage systemMessage = new SystemMessage("""
			You are an exam solution transcription engine.

			TASK:
			Extract and normalize the official exam solutions from the attached PDF into the provided JSON schema.

			STRICT RULES:
			- Only extract information explicitly present in the solutions document.
			- Do NOT invent additional answers, explanations, or grading criteria.
			- Do NOT evaluate quality or assign marks.
			- Preserve original meaning and expected answer content as closely as possible.

			If information is unclear, missing, or unreadable:
			- set the field to null

			Ignore:
			- formatting issues
			- layout artifacts
			- OCR noise
			- repeated or misaligned text

			The goal is structural transcription, not reasoning.

			OUTPUT:
			Must strictly follow JSON schema.
			No extra fields.
			No commentary.

			JSON Schema:

			{
			  "subject": "string",
			
			  "questions": [
			    {
			      "questionId": "string",
			      "modelAnswer": "string",
			      "keyPoints": ["string"],
			      "acceptableAlternativePoints": ["string"],
			      "evidenceReferences": ["string"]
			    }
			  ]
			}
			""");

	UserMessage solutionMessage = UserMessage.builder()
			.text("These are the official exam solutions.")
			.media(new Media(
					MimeTypeUtils.parseMimeType("application/pdf"),
					solutionPdf))
			.build();

	Prompt prompt = new Prompt(List.of(systemMessage, solutionMessage));

	ChatResponse response;

	try {
		response = chatModel.call(prompt);
	} catch (Exception e) {
		throw new RuntimeException("AI solution parsing failed", e);
	}

	var raw = response.getResult().getOutput().getText();

	log.info("====== Response from ai model for solution transcription: ========");
	log.info(raw);

	objectMapper.configure(
			DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
			false);

//	objectMapper.setPropertyNamingStrategy(
//			PropertyNamingStrategies.SNAKE_CASE);

	SolutionDto dto = objectMapper.readValue(raw, SolutionDto.class);

	return dto;
}
	///

	private void validate(ExamEvaluationDto result) {
		if (result == null) {
			throw new IllegalStateException("AI returned null response");
		}

		if (result.marksAwarded() == null || result.maxMarks() == null) {
			throw new IllegalStateException("Missing marks in evaluation");
		}

		if (result.rubricReference() == null) {
			throw new IllegalStateException("Missing rubric reference");
		}
	}
}