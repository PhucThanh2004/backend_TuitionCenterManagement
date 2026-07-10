package com.management.student_center.assistant.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.management.student_center.assistant.model.request.Content;
import com.management.student_center.assistant.model.request.GeminiRequest;
import com.management.student_center.assistant.model.request.Part;
import com.management.student_center.assistant.model.response.GeminiResponse;

@Service
public class GeminiService {

    private final WebClient geminiWebClient;

    @Value("${gemini.model}")
    private String model;

    public GeminiService(WebClient geminiWebClient) {
        this.geminiWebClient = geminiWebClient;
    }

    public String generateContent(String prompt) {

    	Part part = new Part();
    	part.setText(prompt);

    	Content content = new Content();
    	content.setParts(List.of(part));

    	GeminiRequest request = new GeminiRequest();
    	request.setContents(List.of(content));

    	GeminiResponse response = geminiWebClient
    	        .post()
    	        .uri("/models/" + model + ":generateContent")
    	        .bodyValue(request)
    	        .retrieve()
    	        .onStatus(
    	                status -> status.isError(),
    	                clientResponse -> clientResponse.bodyToMono(String.class)
    	                        .map(error -> new RuntimeException(error))
    	        )
    	        .bodyToMono(GeminiResponse.class)
    	        .block();

        if (response == null
                || response.getCandidates() == null
                || response.getCandidates().isEmpty()) {

            return "Xin lỗi, AI hiện không thể trả lời.";
        }

        return response.getCandidates()
                .get(0)
                .getContent()
                .getParts()
                .get(0)
                .getText();
    }
}