package com.phoenix.dto;

import java.time.Instant;
import java.util.List;

public record ChatResponse(

        String answer,

        /** Text excerpts from the documents used to ground the answer. */
        List<String> sourceChunks,

        /** Number of chunks retrieved from the vector store. */
        int chunksUsed,

        Instant timestamp
) {
    public static ChatResponse of(String answer, List<String> sourceChunks) {
        return new ChatResponse(answer, sourceChunks, sourceChunks.size(), Instant.now());
    }
}
