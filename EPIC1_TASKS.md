# Project Phoenix: Epic 1 - Document Ingestion

## Phase 1: Planning
- [/] Create implementation plan for document ingestion
- [ ] Review and get user approval

## Phase 2: Domain Layer
- [ ] Create Document entity
- [ ] Create DocumentChunk entity  
- [ ] Create DocumentRepository
- [ ] Create DocumentChunkRepository

## Phase 3: Service Layer
- [ ] Create DocumentService (upload handling)
- [ ] Create DocumentProcessingService (text extraction, chunking)
- [ ] Create EmbeddingService (vector generation)

## Phase 4: Kafka Integration
- [ ] Create Kafka configuration
- [ ] Create DocumentIngestionProducer
- [ ] Create DocumentIngestionConsumer

## Phase 5: REST API
- [ ] Create DocumentController (upload endpoint)
- [ ] Create DTOs (DocumentUploadRequest, DocumentResponse)

## Phase 6: Testing
- [ ] Unit tests for services
- [ ] Integration test for upload flow

## Phase 7: Verification
- [ ] Push and verify CI passes
- [ ] Test upload endpoint locally
