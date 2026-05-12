package com.exammarker.helloworld;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeTypeUtils;

import com.exammarker.helloworld.dto.ExamEvaluationDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class HelloworldApplication {

	private final ObjectMapper objectMapper = new ObjectMapper();

	public static void main(String[] args) {
		SpringApplication.run(HelloworldApplication.class, args);
	}

//	@Bean
//    CommandLineRunner runner(OpenAiChatModel chatModel) {
//        return args -> {
//            System.out.println("--- Sending request to OpenAI ---");
//            
//            // This is the "Hello World" call
//            String response = chatModel.call("Hello Michael! Say 'Hello World' back to me. And please tell me my name.. hahaha.. mention a song if it comes to you");
//            
//            System.out.println("OpenAI says: " + response);
//        };
//    }

	@Bean
	CommandLineRunner gradeExam(OpenAiChatModel chatModel) {

		return args -> {

			Resource studentWork = new FileSystemResource("/Users/hammadquddus/Downloads/upload-unmarked-papers.pdf");

			Resource solutions = new FileSystemResource("/Users/hammadquddus/Downloads/upload-exam-solutions.pdf");

			Resource rubric = new FileSystemResource("/Users/hammadquddus/Downloads/upload-level-descriptor.pdf");

			SystemMessage systemMessage = new SystemMessage("""
					 	    You are an experienced 9th-grade Islamic Studies teacher.

					 	    Read ALL attached files carefully.

					Tasks:
					1. Read the rubric
					2. Read the exam solutions
					3. Read the student's handwritten paper
					4. Transcribe the student answers
					5. Compare against solutions
					6. Assign marks out of 10
					7. Extract supporting evidence from the student's answer

					Rules:
					- Never invent student answers
					- If handwriting is unreadable, explicitly say so
					- Base grading on the supplied rubric
					- Be strict but fair
					- Evidence excerpts must be copied directly from the student's answer
					- Keep evidence excerpts concise
					- Return ONLY valid JSON
					- Do not return markdown
					- Do not wrap JSON in triple backticks

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

					 	      "evidence": [
					   {
					      "point": string,
					      "studentExcerpt": string
					   }
					  ]

					 	      "confidence": {
					 			"transcriptionConfidence": number,
					 			"gradingConfidence": number
					 		   },
					 		   "requiresHumanReview": boolean
					 	    }
					 	    """);

			UserMessage rubricMessage = UserMessage.builder().text("This is the grading rubric.")
					.media(new Media(MimeTypeUtils.parseMimeType("application/pdf"), rubric)).build();

			UserMessage solutionsMessage = UserMessage.builder().text("These are the official exam solutions.")
					.media(new Media(MimeTypeUtils.parseMimeType("application/pdf"), solutions)).build();

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
					""").media(new Media(MimeTypeUtils.parseMimeType("application/pdf"), studentWork)).build();

			Prompt prompt = new Prompt(List.of(systemMessage, rubricMessage, solutionsMessage, studentMessage));

			ChatResponse response = chatModel.call(prompt);

			var raw = response.getResult().getOutput().getText();
			System.out.println(raw);

			System.out.println("=========================================================");

			// once stable
			ExamEvaluationDto dto = objectMapper.readValue(raw, ExamEvaluationDto.class);
			System.out.println(dto);

		};
	}

}

///Users/hammadquddus/Downloads/upload-unmarked-papers.pdf
