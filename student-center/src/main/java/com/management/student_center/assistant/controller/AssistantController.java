package com.management.student_center.assistant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.management.student_center.assistant.dto.ChatRequest;
import com.management.student_center.assistant.dto.ChatResponse;
import com.management.student_center.assistant.service.PublicAssistantService;

@RestController
@RequestMapping("/v1/api/assistant")
@CrossOrigin
public class AssistantController {

    private final PublicAssistantService publicAssistantService;

    public AssistantController(
            PublicAssistantService publicAssistantService
    ) {
        this.publicAssistantService = publicAssistantService;
    }

    /**
     * Public AI Chat
     * Không cần đăng nhập
     */
    @PostMapping("/public/chat")
    public ResponseEntity<ChatResponse> chat(
            @RequestBody ChatRequest request
    ) {

        String answer = publicAssistantService.chat(
                request.getMessage()
        );

        ChatResponse response = new ChatResponse();
        response.setResponse(answer);

        return ResponseEntity.ok(response);
    }

}