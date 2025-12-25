package com.phoenix.controller;

import com.phoenix.domain.Document;
import com.phoenix.dto.DocumentResponse;
import com.phoenix.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "userId", defaultValue = "anonymous") String userId) {

        try {
            log.info("Received upload request: {} ({} bytes)",
                    file.getOriginalFilename(), file.getSize());

            Document document = documentService.uploadDocument(file, userId);

            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(DocumentResponse.from(document));

        } catch (IllegalArgumentException e) {
            log.warn("Invalid upload request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Upload failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocument(@PathVariable UUID id) {
        return documentService.getDocument(id)
                .map(doc -> ResponseEntity.ok(DocumentResponse.from(doc)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getDocumentsByUser(
            @RequestParam(value = "userId", defaultValue = "anonymous") String userId) {

        List<DocumentResponse> documents = documentService.getDocumentsByUser(userId)
                .stream()
                .map(DocumentResponse::from)
                .toList();

        return ResponseEntity.ok(documents);
    }
}
