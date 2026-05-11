package com.exammarker.helloworld;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HelloworldApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloworldApplication.class, args);
	}

	
	@Bean
    CommandLineRunner runner(OpenAiChatModel chatModel) {
        return args -> {
            System.out.println("--- Sending request to OpenAI ---");
            
            // This is the "Hello World" call
            String response = chatModel.call("Hello Michael! Say 'Hello World' back to me. And please tell me my name.. hahaha.. mention a song if it comes to you");
            
            System.out.println("OpenAI says: " + response);
        };
    }
}
