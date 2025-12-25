# Epic 1: Document Ingestion Pipeline

Implementation plan for secure, async document upload and processing.

---

## Summary

Build an ETL pipeline that:
1. Accepts PDF/DOCX uploads via REST API
2. Publishes ingestion events to Kafka
3. Extracts text using Apache Tika
4. Chunks text into semantic segments
5. Generates embeddings via Ollama
6. Stores chunks + vectors in PostgreSQL/pgvector

---

## Architecture

```
[Upload API] --> [Kafka: document-ingestion] --> [Consumer]
                                                     |
                                         [Tika] --> [Chunker] --> [Embedder]
                                                                      |
                                                              [PostgreSQL/pgvector]
```

---

## Proposed Changes

### Domain Layer

#### [NEW] Document.java
`backend/src/main/java/com/phoenix/domain/Document.java`
- Entity: id, filename, contentType, uploadedBy, status, createdAt

#### [NEW] DocumentChunk.java
`backend/src/main/java/com/phoenix/domain/DocumentChunk.java`
- Entity: id, documentId, content, chunkIndex, embedding (vector)

#### [NEW] DocumentRepository.java
`backend/src/main/java/com/phoenix/repository/DocumentRepository.java`

#### [NEW] DocumentChunkRepository.java
`backend/src/main/java/com/phoenix/repository/DocumentChunkRepository.java`

---

### Service Layer

#### [NEW] DocumentService.java
`backend/src/main/java/com/phoenix/service/DocumentService.java`
- `uploadDocument(MultipartFile, userId)` - saves file, publishes Kafka event

#### [NEW] DocumentProcessingService.java
`backend/src/main/java/com/phoenix/service/DocumentProcessingService.java`
- `processDocument(documentId)` - extracts text, chunks, generates embeddings

---

### Kafka Integration

#### [NEW] KafkaConfig.java
`backend/src/main/java/com/phoenix/config/KafkaConfig.java`
- Topic: `document-ingestion`

#### [NEW] DocumentIngestionProducer.java
`backend/src/main/java/com/phoenix/kafka/DocumentIngestionProducer.java`

#### [NEW] DocumentIngestionConsumer.java
`backend/src/main/java/com/phoenix/kafka/DocumentIngestionConsumer.java`

---

### REST API

#### [NEW] DocumentController.java
`backend/src/main/java/com/phoenix/controller/DocumentController.java`
- `POST /api/documents/upload` - multipart upload, returns 202 Accepted

---

## Verification Plan

1. Unit tests for DocumentService, DocumentProcessingService
2. CI pipeline passes
3. Manual test: upload PDF via curl, verify chunks in database
