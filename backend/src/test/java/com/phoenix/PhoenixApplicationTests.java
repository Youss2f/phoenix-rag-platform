package com.phoenix;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PhoenixApplicationTests {

    @Test
    void contextLoads() {
        // Context load test - verifies Spring Boot starts successfully
    }
}
