package com.exammarker.helloworld;

import java.util.List;

import org.springframework.ai.chat.messages.UserMessage;
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
	        Resource examFile = new FileSystemResource("/Users/hammadquddus/Downloads/upload-unmarked-papers.pdf");


	        var userMessage = UserMessage.builder()
	        		.text("You are an experienced 9th-grade teacher. "
	        				+ "1. Transcribe the handwritten text from this exam image. "
	        				+ "2. Identify the question numbers. "
	        				+ "3. Provide a brief assessment of the student's clarity. "
	        				+ "4.Format the output as a clean Markdown report.")
	        		.media(new Media(MimeTypeUtils.parseMimeType("application/pdf"), examFile))
	        		.build();


	        String report = chatModel.call(new Prompt(userMessage))
	                                 .getResult().getOutput().getText();
	                                 
	        
	        System.out.println("--- Student Exam Report ---\n" + report);
	    };
	}	
	
}

///Users/hammadquddus/Downloads/upload-unmarked-papers.pdf
