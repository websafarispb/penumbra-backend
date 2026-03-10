package com.penumbra.storage;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final MinioClient minioClient;
    private final StorageProperties storageProperties;

    public String uploadFile(MultipartFile file) {
        try {
            String objectName = generateObjectName(file.getOriginalFilename());

            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(storageProperties.getBucket())
                                .object(objectName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }

            return objectName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to storage", e);
        }
    }

    public InputStream downloadFile(String objectName) {
        try {
            return minioClient.getObject(
                    io.minio.GetObjectArgs.builder()
                            .bucket(storageProperties.getBucket())
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from storage", e);
        }
    }

    private String generateObjectName(String originalFilename) {
        String safeFileName = originalFilename == null ? "file" : originalFilename;
        return UUID.randomUUID() + "_" + safeFileName;
    }
}