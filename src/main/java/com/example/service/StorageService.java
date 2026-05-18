package com.example.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * Service for Cloud Storage operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {
    
    private final Storage storage;
    
    @Value("${gcp.storage.bucket-name}")
    private String bucketName;
    
    @Value("${gcp.storage.base-url}")
    private String baseUrl;
    
    /**
     * Upload file to Cloud Storage
     */
    public String uploadFile(MultipartFile file) throws IOException {
        try {
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();
            
            storage.create(blobInfo, file.getInputStream());
            
            String publicUrl = String.format("%s/%s/%s", baseUrl, bucketName, fileName);
            log.info("File uploaded successfully: {}", publicUrl);
            
            return publicUrl;
        } catch (IOException e) {
            log.error("Error uploading file to Cloud Storage", e);
            throw e;
        }
    }
    
    /**
     * Delete file from Cloud Storage
     */
    public boolean deleteFile(String fileUrl) {
        try {
            // Extract file name from URL
            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            BlobId blobId = BlobId.of(bucketName, fileName);
            
            boolean deleted = storage.delete(blobId);
            
            if (deleted) {
                log.info("File deleted successfully: {}", fileName);
            } else {
                log.warn("File not found for deletion: {}", fileName);
            }
            
            return deleted;
        } catch (Exception e) {
            log.error("Error deleting file from Cloud Storage", e);
            return false;
        }
    }
    
    /**
     * Generate unique file name with UUID prefix
     */
    private String generateUniqueFileName(String originalFileName) {
        String uuid = UUID.randomUUID().toString();
        return uuid + "_" + originalFileName;
    }
}
