package com.smarttourism.smart_tourism_backend.dto;

import com.smarttourism.smart_tourism_backend.service.GeminiService.AiChatMessage;

import java.util.List;

public record AiChatRequest(String message, List<AiChatMessage> history) {
}
