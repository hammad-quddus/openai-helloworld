package com.exammarker.client.openai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import com.exammarker.dto.evaluation.ExamEvaluationDto;

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