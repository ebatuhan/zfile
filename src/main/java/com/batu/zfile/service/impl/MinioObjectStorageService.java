package com.batu.zfile.service.impl;

import java.io.InputStream;
import java.util.Collection;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.batu.zfile.config.MinioProperties;
import com.batu.zfile.entity.FileMetadata;
import com.batu.zfile.exception.FileContentStorageException;
import com.batu.zfile.service.ObjectBucket;
import com.batu.zfile.service.ObjectStorageService;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.GetObjectArgs;
import io.minio.Http;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
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
                    .bucket(minioProperties.fileBucket())
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
    public InputStream read(ObjectBucket bucket, String objectKey) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket == ObjectBucket.FILE ? minioProperties.fileBucket() : minioProperties.thumbnailBucket())
                    .object(objectKey)
                    .build());
        } catch (Exception exception) {
            throw new FileContentStorageException("Failed to read object content", exception);
        }
    }

    @Override
    public void upload(ObjectBucket bucket, String objectKey, InputStream content, long size, String contentType) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket == ObjectBucket.FILE ? minioProperties.fileBucket() : minioProperties.thumbnailBucket())
                    .object(objectKey)
                    .stream(content, size, -1L)
                    .contentType(contentType == null ? DEFAULT_CONTENT_TYPE : contentType)
                    .build());
        } catch (Exception exception) {
            throw new FileContentStorageException("Failed to upload object content", exception);
        }
    }

    @Override
    public String createPresignedUrl(ObjectBucket bucket, String objectKey) {
        try {
            return publicMinioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Http.Method.GET)
                    .bucket(bucket == ObjectBucket.FILE ? minioProperties.fileBucket() : minioProperties.thumbnailBucket())
                    .object(objectKey)
                    .expiry(minioProperties.presignedUrlExpirySeconds())
                    .build());
        } catch (Exception exception) {
            throw new FileContentStorageException("Failed to create object URL", exception);
        }
    }

    @Override
    public void deleteAll(ObjectBucket bucket, Collection<String> objectKeys) {
        if (objectKeys.isEmpty()) {
            return;
        }

        var objects = objectKeys.stream()
                .map(DeleteRequest.Object::new)
                .toList();

        try {
            var results = minioClient.removeObjects(RemoveObjectsArgs.builder()
                    .bucket(bucket == ObjectBucket.FILE ? minioProperties.fileBucket() : minioProperties.thumbnailBucket())
                    .objects(objects)
                    .build());

            for (var result : results) {
                var error = result.get();
                throw new FileContentStorageException("Failed to delete object content: " + error.objectName(), null);
            }
        } catch (FileContentStorageException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new FileContentStorageException("Failed to delete object contents", exception);
        }
    }
}
