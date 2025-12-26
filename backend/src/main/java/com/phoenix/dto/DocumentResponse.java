package com.phoenix.dto;

import com.phoenix.domain.Document;
import java.time.Instant;
import java.util.UUID;

public record DocumentResponse(
        UUID id,
        String filename,
        String contentType,
        Long fileSize,
        String status,
        Instant createdAt,
        Instant processedAt) {
    public static DocumentResponse from(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getFilename(),
                document.getContentType(),
                document.getFileSize(),
                document.getStatus().name(),
                document.getCreatedAt(),
                document.getProcessedAt());
    }
}
