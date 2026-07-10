package com.management.student_center.assistant.service;

import org.springframework.stereotype.Service;

import com.management.student_center.assistant.util.PromptLoader;

@Service
public class PublicAssistantService {

	private final GeminiService geminiService;
	private final PromptLoader promptLoader;
	private final PublicKnowledgeService publicKnowledgeService;

	public PublicAssistantService(GeminiService geminiService, PromptLoader promptLoader,
			PublicKnowledgeService publicKnowledgeService) {

		this.geminiService = geminiService;
		this.promptLoader = promptLoader;
		this.publicKnowledgeService = publicKnowledgeService;
	}

	public String chat(String userMessage) {

		String systemPrompt = promptLoader.loadPrompt("assistant/prompts/public_prompt.txt");

		String knowledge = publicKnowledgeService.buildContext();

		String finalPrompt = systemPrompt + "\n\n" + "Dữ liệu thực tế của trung tâm:\n" + knowledge + "\n\n"
				+ "Câu hỏi của người dùng:\n" + userMessage;

		return geminiService.generateContent(finalPrompt);
	}
}