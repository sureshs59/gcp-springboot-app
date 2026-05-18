package com.example.repository;

import com.example.model.Document;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository for Firestore document operations
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class DocumentRepository {
    
    private final Firestore firestore;
    private static final String COLLECTION = "documents";
    
    /**
     * Save document to Firestore
     */
    public String save(Document document) {
        try {
            com.google.cloud.firestore.DocumentReference docRef = firestore.collection(COLLECTION).document();
            document.setId(docRef.getId());
            docRef.set(document).get();
            log.info("Document saved with ID: {}", docRef.getId());
            return docRef.getId();
        } catch (Exception e) {
            log.error("Error saving document", e);
            throw new RuntimeException("Error saving document", e);
        }
    }
    
    /**
     * Find document by ID
     */
    public Optional<Document> findById(String id) {
        try {
            com.google.cloud.firestore.DocumentSnapshot doc = 
                firestore.collection(COLLECTION).document(id).get().get();
            
            if (doc.exists()) {
                return Optional.of(doc.toObject(Document.class));
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error finding document by ID: {}", id, e);
            throw new RuntimeException("Error finding document", e);
        }
    }
    
    /**
     * Find all documents
     */
    public List<Document> findAll() {
        try {
            QuerySnapshot querySnapshot = firestore.collection(COLLECTION).get().get();
            return querySnapshot.getDocuments().stream()
                    .map(doc -> doc.toObject(Document.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error retrieving all documents", e);
            throw new RuntimeException("Error retrieving documents", e);
        }
    }
    
    /**
     * Update document
     */
    public void update(String id, Document document) {
        try {
            firestore.collection(COLLECTION).document(id).set(document).get();
            log.info("Document updated: {}", id);
        } catch (Exception e) {
            log.error("Error updating document: {}", id, e);
            throw new RuntimeException("Error updating document", e);
        }
    }
    
    /**
     * Delete document
     */
    public void delete(String id) {
        try {
            firestore.collection(COLLECTION).document(id).delete().get();
            log.info("Document deleted: {}", id);
        } catch (Exception e) {
            log.error("Error deleting document: {}", id, e);
            throw new RuntimeException("Error deleting document", e);
        }
    }
}
