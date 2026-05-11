package com.exammarker.helloworld;

import java.util.List;

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
	        Resource studentWork = new FileSystemResource("/Users/hammadquddus/Downloads/upload-unmarked-papers.pdf");
	        Resource text_reading = new FileSystemResource("/Users/hammadquddus/Downloads/upload-exam-solutions.pdf");
	        Resource rubric = new FileSystemResource("/Users/hammadquddus/Downloads/upload-level-descriptor.pdf");
	        
	        var systemMessage = UserMessage.builder()
	        		.text("You are an experienced 9th-grade Islamic studies teacher. "
	        				+ "Use the provided 'Level Descriptor' to determine the grade (marks out of 10).\n"
	        				+ "Use the provided 'Exam Solution / reference reading' as the source of factual truth.")	        				
	        		.media(new Media(MimeTypeUtils.parseMimeType("application/pdf"), rubric))     // upload-level-descriptor.pdf [cite: 110]
	        	    .media(new Media(MimeTypeUtils.parseMimeType("application/pdf"), text_reading))    // upload-exam-solutions.pdf [cite: 68]
	        		.build();

	        var userMessage = UserMessage.builder()
	        		.text("Please grade the attached exam for the student.")
	        	    .text("1. Transcribe the handwritten text from the student's paper page by page.")
	        	    .text("2. Provide a brief assessment of the student's clarity and factual accuracy.")
	        	    .text("3. Format the output as a clean Markdown report based on the content found.")	        	    .media(new Media(MimeTypeUtils.parseMimeType("application/pdf"), studentWork))
	        		.build();


	        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
	        ChatResponse response = chatModel.call(prompt);
	        String report = response.getResult().getOutput().getText();
	                                 
	        
	        System.out.println("--- Student Exam Report ---\n" + report);
	    };
	}	
	
}

///Users/hammadquddus/Downloads/upload-unmarked-papers.pdf
