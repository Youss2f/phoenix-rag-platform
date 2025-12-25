package com.phoenix;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = {
        KafkaAutoConfiguration.class
})
class PhoenixApplicationTests {

    @Test
    void contextLoads() {
        // Verifies Spring Boot starts successfully with test profile
    }
}
