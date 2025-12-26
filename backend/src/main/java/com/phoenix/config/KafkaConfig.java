package com.phoenix.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("!test")
public class KafkaConfig {

    public static final String DOCUMENT_INGESTION_TOPIC = "document-ingestion";

    @Bean
    public NewTopic documentIngestionTopic() {
        return TopicBuilder.name(DOCUMENT_INGESTION_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
