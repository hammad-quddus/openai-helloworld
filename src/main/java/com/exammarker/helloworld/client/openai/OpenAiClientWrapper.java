package com.exammarker.helloworld.client.openai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import com.exammarker.helloworld.dto.ExamEvaluationDto;

@Component
public class OpenAiClientWrapper {

    private final ChatClient chatClient;

    public OpenAiClientWrapper(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public ExamEvaluationDto generate(String prompt) {

        return chatClient.prompt()
            .system("""
                You are a strict grading engine.
                Return output that matches the required structure exactly.
                No extra fields. No explanation.
            """)
            .user(prompt)
            .call()
            .entity(ExamEvaluationDto.class);
    }
}