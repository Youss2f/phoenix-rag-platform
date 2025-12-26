package com.phoenix.service;

import com.phoenix.domain.Document;
import com.phoenix.domain.DocumentChunk;
import com.phoenix.repository.DocumentChunkRepository;
import com.phoenix.repository.DocumentRepository;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentProcessingService {

    private static final Logger log = LoggerFactory.getLogger(DocumentProcessingService.class);
    private static final int CHUNK_SIZE = 1000;
    private static final int CHUNK_OVERLAP = 200;

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository chunkRepository;
    private final DocumentService documentService;
    private final Tika tika;

    public DocumentProcessingService(
            DocumentRepository documentRepository,
            DocumentChunkRepository chunkRepository,
            DocumentService documentService) {
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
        this.documentService = documentService;
        this.tika = new Tika();
    }

    @Transactional
    public void processDocument(UUID documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));

        try {
            log.info("Processing document: {} ({})", document.getFilename(), documentId);

            // Update status
            document.setStatus(Document.DocumentStatus.PROCESSING);
            documentRepository.save(document);

            // Extract text
            Path filePath = documentService.getDocumentPath(documentId);
            String text = extractText(filePath);

            if (text == null || text.isBlank()) {
                throw new RuntimeException("No text content extracted from document");
            }

            log.info("Extracted {} characters from document", text.length());

            // Chunk text
            List<String> chunks = chunkText(text);
            log.info("Created {} chunks from document", chunks.size());

            // Save chunks (embeddings will be added later with Spring AI)
            for (int i = 0; i < chunks.size(); i++) {
                DocumentChunk chunk = new DocumentChunk();
                chunk.setDocument(document);
                chunk.setContent(chunks.get(i));
                chunk.setChunkIndex(i);
                chunkRepository.save(chunk);
            }

            // Update status
            document.setStatus(Document.DocumentStatus.COMPLETED);
            document.setProcessedAt(Instant.now());
            documentRepository.save(document);

            log.info("Successfully processed document: {}", documentId);

        } catch (Exception e) {
            log.error("Failed to process document {}: {}", documentId, e.getMessage(), e);
            document.setStatus(Document.DocumentStatus.FAILED);
            documentRepository.save(document);
        }
    }

    private String extractText(Path filePath) throws IOException, TikaException {
        return tika.parseToString(filePath);
    }

    private List<String> chunkText(String text) {
        List<String> chunks = new ArrayList<>();

        if (text.length() <= CHUNK_SIZE) {
            chunks.add(text.trim());
            return chunks;
        }

        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + CHUNK_SIZE, text.length());

            // Try to break at paragraph or sentence boundary
            if (end < text.length()) {
                int paragraphBreak = text.lastIndexOf("\n\n", end);
                int sentenceBreak = text.lastIndexOf(". ", end);

                if (paragraphBreak > start + CHUNK_SIZE / 2) {
                    end = paragraphBreak + 2;
                } else if (sentenceBreak > start + CHUNK_SIZE / 2) {
                    end = sentenceBreak + 2;
                }
            }

            String chunk = text.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }

            start = end - CHUNK_OVERLAP;
            if (start < 0)
                start = 0;
            if (start >= text.length())
                break;
        }

        return chunks;
    }
}
