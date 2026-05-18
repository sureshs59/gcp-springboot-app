package com.example.service;

import com.example.model.Document;
import com.example.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for document operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {
    
    private final DocumentRepository documentRepository;
    private final StorageService storageService;
    
    /**
     * Create new document with optional file
     */
    public Document createDocument(String title, String description, MultipartFile file) {
        try {
            String fileUrl = null;
            String fileName = null;
            Long fileSize = null;
            
            if (file != null && !file.isEmpty()) {
                fileUrl = storageService.uploadFile(file);
                fileName = file.getOriginalFilename();
                fileSize = file.getSize();
                log.info("File uploaded: {} ({} bytes)", fileName, fileSize);
            }
            
            Document document = Document.builder()
                    .title(title)
                    .description(description)
                    .fileName(fileName)
                    .fileSize(fileSize)
                    .fileUrl(fileUrl)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            String documentId = documentRepository.save(document);
            document.setId(documentId);
            
            log.info("Document created with ID: {}", documentId);
            return document;
        } catch (Exception e) {
            log.error("Error creating document", e);
            throw new RuntimeException("Error creating document", e);
        }
    }
    
    /**
     * Get document by ID
     */
    public Optional<Document> getDocumentById(String id) {
        log.info("Retrieving document: {}", id);
        return documentRepository.findById(id);
    }
    
    /**
     * Get all documents
     */
    public List<Document> getAllDocuments() {
        log.info("Retrieving all documents");
        return documentRepository.findAll();
    }
    
    /**
     * Update document
     */
    public Document updateDocument(String id, String title, String description) {
        try {
            Optional<Document> existingDoc = documentRepository.findById(id);
            
            if (existingDoc.isEmpty()) {
                throw new RuntimeException("Document not found: " + id);
            }
            
            Document document = existingDoc.get();
            document.setTitle(title != null ? title : document.getTitle());
            document.setDescription(description != null ? description : document.getDescription());
            document.setUpdatedAt(LocalDateTime.now());
            
            documentRepository.update(id, document);
            log.info("Document updated: {}", id);
            
            return document;
        } catch (Exception e) {
            log.error("Error updating document", e);
            throw new RuntimeException("Error updating document", e);
        }
    }
    
    /**
     * Delete document
     */
    public void deleteDocument(String id) {
        try {
            Optional<Document> document = documentRepository.findById(id);
            
            if (document.isEmpty()) {
                throw new RuntimeException("Document not found: " + id);
            }
            
            // Delete associated file from Cloud Storage
            if (document.get().getFileUrl() != null) {
                storageService.deleteFile(document.get().getFileUrl());
                log.info("File deleted for document: {}", id);
            }
            
            documentRepository.delete(id);
            log.info("Document deleted: {}", id);
        } catch (Exception e) {
            log.error("Error deleting document", e);
            throw new RuntimeException("Error deleting document", e);
        }
    }
}
