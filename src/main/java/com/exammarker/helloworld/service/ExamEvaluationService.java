package com.exammarker.helloworld.service;

import com.exammarker.helloworld.dto.ExamEvaluationDto;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ExamEvaluationService {

    private final ChatClient chatClient;

    public ExamEvaluationService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public ExamEvaluationDto evaluate(String prompt) {

        ExamEvaluationDto result = chatClient.prompt()
            .system("""
                You are a strict exam grading engine.

                Rules:
                - Return ONLY valid JSON.
                - Must match the ExamEvaluationDto structure exactly.
                - No extra keys.
                - No explanations or markdown.
                - Be consistent and deterministic in grading.
            """)
            .user(prompt)
            .call()
            .entity(ExamEvaluationDto.class);

        validate(result);

        return result;
    }

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