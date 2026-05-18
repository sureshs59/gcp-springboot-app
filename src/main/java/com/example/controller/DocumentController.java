package com.example.controller;

import com.example.dto.ApiResponse;
import com.example.model.Document;
import com.example.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * REST API controller for document operations
 */
@Slf4j
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DocumentController {
    
    private final DocumentService documentService;
    
    /**
     * Create new document
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Document>> createDocument(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) MultipartFile file) {
        try {
            Document document = documentService.createDocument(title, description, file);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Document created successfully", 201, document));
        } catch (Exception e) {
            log.error("Error creating document", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error creating document: " + e.getMessage(), 500));
        }
    }
    
    /**
     * Get all documents
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Document>>> getAllDocuments() {
        try {
            List<Document> documents = documentService.getAllDocuments();
            return ResponseEntity.ok(ApiResponse.success("Documents retrieved successfully", 200, documents));
        } catch (Exception e) {
            log.error("Error retrieving documents", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving documents", 500));
        }
    }
    
    /**
     * Get document by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Document>> getDocumentById(@PathVariable String id) {
        try {
            Optional<Document> document = documentService.getDocumentById(id);
            if (document.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Document found", 200, document.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Document not found", 404));
            }
        } catch (Exception e) {
            log.error("Error retrieving document", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving document", 500));
        }
    }
    
    /**
     * Update document
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Document>> updateDocument(
            @PathVariable String id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description) {
        try {
            Document document = documentService.updateDocument(id, title, description);
            return ResponseEntity.ok(ApiResponse.success("Document updated successfully", 200, document));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(e.getMessage(), 404));
            }
            log.error("Error updating document", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error updating document", 500));
        }
    }
    
    /**
     * Delete document
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable String id) {
        try {
            documentService.deleteDocument(id);
            return ResponseEntity.ok(ApiResponse.success("Document deleted successfully", 200, null));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(e.getMessage(), 404));
            }
            log.error("Error deleting document", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error deleting document", 500));
        }
    }
}
