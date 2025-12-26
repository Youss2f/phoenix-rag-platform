package com.phoenix.repository;

import com.phoenix.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    List<Document> findByUploadedBy(String uploadedBy);

    List<Document> findByStatus(Document.DocumentStatus status);
}
