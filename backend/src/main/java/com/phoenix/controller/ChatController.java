package com.phoenix.controller;

import com.phoenix.dto.ChatRequest;
import com.phoenix.dto.ChatResponse;
import com.phoenix.service.ChatService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoint for RAG-based question answering.
 *
 * POST /api/chat
 * Body: { "question": "...", "documentId": "<uuid or null>" }
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        log.info("Chat request: question='{}', documentId={}",
                request.question(), request.documentId());

        try {
            ChatResponse response = chatService.answer(request.question(), request.documentId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid chat request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Chat request failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
