package com.exammarker.helloworld;

import java.util.List;

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
import org.springframework.messaging.Message;
import org.springframework.util.MimeTypeUtils;

@SpringBootApplication
public class HelloworldApplication {

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

	        Resource studentWork =
	                new FileSystemResource("/Users/hammadquddus/Downloads/upload-unmarked-papers.pdf");

	        Resource solutions =
	                new FileSystemResource("/Users/hammadquddus/Downloads/upload-exam-solutions.pdf");

	        Resource rubric =
	                new FileSystemResource("/Users/hammadquddus/Downloads/upload-level-descriptor.pdf");

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

	            Never invent student answers.
	            If handwriting is unreadable, say so.
	            """);

	        UserMessage rubricMessage = UserMessage.builder()
	                .text("This is the grading rubric.")
	                .media(new Media(
	                        MimeTypeUtils.parseMimeType("application/pdf"),
	                        rubric))
	                .build();

	        UserMessage solutionsMessage = UserMessage.builder()
	                .text("These are the official exam solutions.")
	                .media(new Media(
	                        MimeTypeUtils.parseMimeType("application/pdf"),
	                        solutions))
	                .build();

	        UserMessage studentMessage = UserMessage.builder()
	                .text("""
	                    This is the student's handwritten exam paper.

	                    Please:
	                    - transcribe answers
	                    - assess accuracy
	                    - assign marks
	                    - generate markdown report
	                    """)
	                .media(new Media(
	                        MimeTypeUtils.parseMimeType("application/pdf"),
	                        studentWork))
	                .build();

	        Prompt prompt = new Prompt(List.of(
	                systemMessage,
	                rubricMessage,
	                solutionsMessage,
	                studentMessage
	        ));

	        ChatResponse response = chatModel.call(prompt);

	        System.out.println(response.getResult().getOutput().getText());
	    };
	}
}

///Users/hammadquddus/Downloads/upload-unmarked-papers.pdf
