package com.example.model;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Document entity model for Firestore
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    
    @DocumentId
    private String id;
    
    private String title;
    private String description;
    private String fileName;
    private Long fileSize;
    private String fileUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
