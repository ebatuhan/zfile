package com.batu.zfile.service.impl;

import java.util.Collection;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.batu.zfile.config.MinioProperties;
import com.batu.zfile.entity.FileMetadata;
import com.batu.zfile.exception.FileContentStorageException;
import com.batu.zfile.service.ObjectStorageService;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.Http;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.messages.DeleteRequest;

@Service
public class MinioObjectStorageService implements ObjectStorageService {

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private final MinioClient minioClient;
    private final MinioClient publicMinioClient;
    private final MinioProperties minioProperties;

    public MinioObjectStorageService(MinioClient minioClient, MinioClient publicMinioClient, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.publicMinioClient = publicMinioClient;
        this.minioProperties = minioProperties;
    }

    @Override
    public FileMetadata upload(MultipartFile file) {
        var objectKey = "files/" + UUID.randomUUID();
        var contentType = file.getContentType() == null ? DEFAULT_CONTENT_TYPE : file.getContentType();

        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.bucket())
                    .object(objectKey)
                    .stream(file.getInputStream(), file.getSize(), -1L)
                    .contentType(contentType)
                    .build());

            return FileMetadata.builder()
                    .objectKey(objectKey)
                    .size(file.getSize())
                    .contentType(contentType)
                    .build();
        } catch (Exception exception) {
            throw new FileContentStorageException("Failed to upload file content", exception);
        }
    }

    @Override
    public String createDownloadUrl(String objectKey) {
        try {
            return publicMinioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Http.Method.GET)
                    .bucket(minioProperties.bucket())
                    .object(objectKey)
                    .expiry(minioProperties.presignedUrlExpirySeconds())
                    .build());
        } catch (Exception exception) {
            throw new FileContentStorageException("Failed to create file download URL", exception);
        }
    }

    @Override
    public void delete(String objectKey) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioProperties.bucket())
                    .object(objectKey)
                    .build());
        } catch (Exception exception) {
            throw new FileContentStorageException("Failed to delete file content", exception);
        }
    }

    @Override
    public void deleteAll(Collection<String> objectKeys) {
        if (objectKeys.isEmpty()) {
            return;
        }

        var objects = objectKeys.stream()
                .map(DeleteRequest.Object::new)
                .toList();

        try {
            var results = minioClient.removeObjects(RemoveObjectsArgs.builder()
                    .bucket(minioProperties.bucket())
                    .objects(objects)
                    .build());

            for (var result : results) {
                var error = result.get();
                throw new FileContentStorageException("Failed to delete file content: " + error.objectName(), null);
            }
        } catch (FileContentStorageException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new FileContentStorageException("Failed to delete file contents", exception);
        }
    }
}
