package com.phoenix.service;

import com.phoenix.domain.Document;
import com.phoenix.dto.DocumentIngestionEvent;
import com.phoenix.kafka.DocumentIngestionProducer;
import com.phoenix.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentRepository documentRepository;
    private final Optional<DocumentIngestionProducer> ingestionProducer;
    private final Path uploadDirectory;

    public DocumentService(
            DocumentRepository documentRepository,
            Optional<DocumentIngestionProducer> ingestionProducer,
            @Value("${phoenix.upload.directory:${java.io.tmpdir}/phoenix-uploads}") String uploadDir) {
        this.documentRepository = documentRepository;
        this.ingestionProducer = ingestionProducer;
        this.uploadDirectory = Path.of(uploadDir);

        try {
            Files.createDirectories(this.uploadDirectory);
        } catch (IOException e) {
            log.error("Failed to create upload directory: {}", e.getMessage());
        }
    }

    @Transactional
    public Document uploadDocument(MultipartFile file, String uploadedBy) throws IOException {
        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (!isValidContentType(contentType)) {
            throw new IllegalArgumentException("Invalid file type. Only PDF and DOCX are allowed.");
        }

        // Create document record
        Document document = new Document();
        document.setFilename(file.getOriginalFilename());
        document.setContentType(contentType);
        document.setFileSize(file.getSize());
        document.setUploadedBy(uploadedBy);
        document.setStatus(Document.DocumentStatus.PENDING);

        Document savedDocument = documentRepository.save(document);

        // Save file to disk
        Path filePath = uploadDirectory.resolve(savedDocument.getId().toString());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        log.info("Uploaded document: {} ({})", savedDocument.getFilename(), savedDocument.getId());

        // Publish ingestion event
        ingestionProducer.ifPresent(producer -> producer.sendIngestionEvent(new DocumentIngestionEvent(
                savedDocument.getId(),
                savedDocument.getFilename(),
                savedDocument.getUploadedBy())));

        return savedDocument;
    }

    public Optional<Document> getDocument(UUID id) {
        return documentRepository.findById(id);
    }

    public List<Document> getDocumentsByUser(String uploadedBy) {
        return documentRepository.findByUploadedBy(uploadedBy);
    }

    public Path getDocumentPath(UUID documentId) {
        return uploadDirectory.resolve(documentId.toString());
    }

    private boolean isValidContentType(String contentType) {
        return contentType != null && (contentType.equals("application/pdf") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                contentType.equals("application/msword"));
    }
}
