package com.phoenix.dto;

import java.util.UUID;

public record DocumentIngestionEvent(
        UUID documentId,
        String filename,
        String uploadedBy) {
}
