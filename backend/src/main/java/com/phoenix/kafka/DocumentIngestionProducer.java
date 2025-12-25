package com.phoenix.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.config.KafkaConfig;
import com.phoenix.dto.DocumentIngestionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class DocumentIngestionProducer {

    private static final Logger log = LoggerFactory.getLogger(DocumentIngestionProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public DocumentIngestionProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendIngestionEvent(DocumentIngestionEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(KafkaConfig.DOCUMENT_INGESTION_TOPIC, event.documentId().toString(), payload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to send ingestion event for document {}: {}",
                                    event.documentId(), ex.getMessage());
                        } else {
                            log.info("Sent ingestion event for document {} to partition {}",
                                    event.documentId(), result.getRecordMetadata().partition());
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize ingestion event: {}", e.getMessage());
        }
    }
}
