package com.phoenix.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.config.KafkaConfig;
import com.phoenix.dto.DocumentIngestionEvent;
import com.phoenix.service.DocumentProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class DocumentIngestionConsumer {

    private static final Logger log = LoggerFactory.getLogger(DocumentIngestionConsumer.class);

    private final DocumentProcessingService processingService;
    private final ObjectMapper objectMapper;

    public DocumentIngestionConsumer(DocumentProcessingService processingService, ObjectMapper objectMapper) {
        this.processingService = processingService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = KafkaConfig.DOCUMENT_INGESTION_TOPIC, groupId = "phoenix-ingestion-group")
    public void consume(String message) {
        try {
            DocumentIngestionEvent event = objectMapper.readValue(message, DocumentIngestionEvent.class);
            log.info("Received ingestion event for document: {}", event.documentId());

            processingService.processDocument(event.documentId());

        } catch (Exception e) {
            log.error("Error processing ingestion event: {}", e.getMessage(), e);
        }
    }
}
