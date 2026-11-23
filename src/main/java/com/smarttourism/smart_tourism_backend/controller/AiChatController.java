package com.smarttourism.smart_tourism_backend.controller;

import com.smarttourism.smart_tourism_backend.dto.AiChatRequest;
import com.smarttourism.smart_tourism_backend.service.GeminiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiChatController {

    private final GeminiService geminiService;

    public AiChatController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody AiChatRequest request) {
        String response = geminiService.getChatResponse(request.message(), request.history());
        return ResponseEntity.ok(Map.of("response", response));
    }
}
