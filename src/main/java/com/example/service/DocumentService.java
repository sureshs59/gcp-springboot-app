package com.example.service;

import com.example.model.Document;
import com.example.repository.DocumentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Service class for Document business logic
 */
@Slf4j
@Service
@AllArgsConstructor
public class DocumentService {
    
    private final DocumentRepository documentRepository;
    private final StorageService storageService;

    /**
     * Create a new document with optional file
     */
    public Document createDocument(String title, String description, MultipartFile file) 
            throws ExecutionException, InterruptedException {
        try {
            Document document = new Document();
            document.setTitle(title);
            document.setDescription(description);
            document.setCreatedAt(LocalDateTime.now());
            document.setUpdatedAt(LocalDateTime.now());

            // Handle file upload if provided
            if (file != null && !file.isEmpty()) {
                try {
                    String fileUrl = storageService.uploadFile(file);
                    document.setFileName(file.getOriginalFilename());
                    document.setFileSize(file.getSize());
                    document.setFileUrl(fileUrl);
                    log.info("File attached to document: {}", file.getOriginalFilename());
                } catch (Exception e) {
                    log.error("Error uploading file", e);
                    throw new RuntimeException("File upload failed", e);
                }
            }

            return documentRepository.save(document);

        } catch (Exception e) {
            log.error("Error creating document", e);
            throw e;
        }
    }

    /**
     * Get all documents
     */
    public List<Document> getAllDocuments() throws ExecutionException, InterruptedException {
        try {
            return documentRepository.findAll();
        } catch (Exception e) {
            log.error("Error retrieving all documents", e);
            throw e;
        }
    }

    /**
     * Get document by ID
     */
    public Optional<Document> getDocumentById(String id) throws ExecutionException, InterruptedException {
        try {
            return documentRepository.findById(id);
        } catch (Exception e) {
            log.error("Error retrieving document with ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Update document
     */
    public Document updateDocument(String id, String title, String description) 
            throws ExecutionException, InterruptedException {
        try {
            Optional<Document> existing = documentRepository.findById(id);
            if (existing.isPresent()) {
                Document document = existing.get();
                document.setTitle(title);
                document.setDescription(description);
                document.setUpdatedAt(LocalDateTime.now());
                return documentRepository.update(id, document);
            } else {
                throw new RuntimeException("Document not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error updating document with ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Delete document
     */
    public void deleteDocument(String id) throws ExecutionException, InterruptedException {
        try {
            Optional<Document> document = documentRepository.findById(id);
            if (document.isPresent()) {
                // Delete associated file from Cloud Storage
                if (document.get().getFileUrl() != null && !document.get().getFileUrl().isEmpty()) {
                    storageService.deleteFile(document.get().getFileUrl());
                }
                documentRepository.deleteById(id);
                log.info("Document deleted with ID: {}", id);
            } else {
                throw new RuntimeException("Document not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error deleting document with ID: {}", id, e);
            throw e;
        }
    }
}
