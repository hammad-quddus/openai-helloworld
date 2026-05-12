package com.exammarker.helloworld;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeTypeUtils;

import com.exammarker.helloworld.dto.ExamEvaluationDto;
import com.exammarker.helloworld.service.ExamEvaluationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class HelloworldApplication {

	@Autowired
	public ExamEvaluationService service;

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
	CommandLineRunner gradeExam() {

		return args -> {

			String studentWork = "/Users/hammadquddus/Downloads/upload-unmarked-papers.pdf";

			String solutions = "/Users/hammadquddus/Downloads/upload-exam-solutions.pdf";

			String rubric = "/Users/hammadquddus/Downloads/upload-level-descriptor.pdf";

			service.evaluate(studentWork, rubric, solutions);

		};
	}

}
