package com.phoenix.service;

import com.phoenix.domain.DocumentChunk;
import com.phoenix.dto.ChatResponse;
import com.phoenix.repository.DocumentChunkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * RAG service: embeds the query, retrieves the nearest document chunks via
 * cosine similarity, injects them into a prompt, and calls the LLM.
 */
@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    /** Number of chunks to retrieve for each query. */
    private static final int TOP_K = 5;

    private final EmbeddingModel embeddingModel;
    private final DocumentChunkRepository chunkRepository;
    private final ChatClient chatClient;

    public ChatService(EmbeddingModel embeddingModel,
                       DocumentChunkRepository chunkRepository,
                       ChatClient.Builder chatClientBuilder) {
        this.embeddingModel = embeddingModel;
        this.chunkRepository = chunkRepository;
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Answer a question using RAG.
     *
     * @param question   The user's question.
     * @param documentId Optional — when non-null, retrieval is scoped to that document.
     */
    public ChatResponse answer(String question, String documentId) {
        log.info("RAG query: '{}' (documentId={})", question, documentId);

        // 1. Embed the query
        float[] queryVector = embeddingModel.embed(question);

        // 2. Retrieve candidate chunks
        List<DocumentChunk> candidates = documentId != null
                ? chunkRepository.findByDocumentIdOrderByChunkIndex(UUID.fromString(documentId))
                : chunkRepository.findAll();

        if (candidates.isEmpty()) {
            log.warn("No indexed chunks found — returning empty-context response");
            String noContextAnswer = chatClient.prompt()
                    .user(question)
                    .call()
                    .content();
            return ChatResponse.of(noContextAnswer, List.of());
        }

        // 3. Rank by cosine similarity and take TOP_K
        List<DocumentChunk> topChunks = candidates.stream()
                .filter(c -> c.getEmbedding() != null)
                .sorted(Comparator.comparingDouble(
                        (DocumentChunk c) -> cosineSimilarity(queryVector, c.getEmbedding())
                ).reversed())
                .limit(TOP_K)
                .collect(Collectors.toList());

        List<String> sourceTexts = topChunks.stream()
                .map(DocumentChunk::getContent)
                .collect(Collectors.toList());

        log.info("Retrieved {} chunks for query", topChunks.size());

        // 4. Build grounded prompt
        String context = String.join("\n\n---\n\n", sourceTexts);
        String prompt = """
                You are a helpful assistant. Answer the user's question using only the
                context provided below. If the answer is not in the context, say so clearly.

                CONTEXT:
                %s

                QUESTION:
                %s
                """.formatted(context, question);

        // 5. Call the LLM
        String answer = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        log.info("Generated answer ({} chars) from {} source chunks", answer.length(), topChunks.size());
        return ChatResponse.of(answer, sourceTexts);
    }

    /**
     * Cosine similarity between two float vectors.
     * Returns a value in [-1, 1] — higher means more similar.
     */
    private double cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) return 0.0;
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += (double) a[i] * b[i];
            normA += (double) a[i] * a[i];
            normB += (double) b[i] * b[i];
        }
        if (normA == 0.0 || normB == 0.0) return 0.0;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
