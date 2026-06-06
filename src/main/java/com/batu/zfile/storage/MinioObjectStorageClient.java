package com.batu.zfile.storage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.Http.Method;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;

import com.batu.zfile.storage.config.MinioConfiguration.MinioProperties;
import com.batu.zfile.storage.enums.BucketType;

import jakarta.annotation.PostConstruct;

@Component
public class MinioObjectStorageClient implements ObjectStorageClient {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public MinioObjectStorageClient(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    @PostConstruct
    @Override
    public void ensureBucketsExist() {
        ensureBucketExists(bucketName(BucketType.FILE));
        ensureBucketExists(bucketName(BucketType.THUMBNAIL));
    }

    @Override
    public PresignedUrl getObjectLink(BucketType bucketType, String objectKey) {
        try {
            String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName(bucketType))
                    .object(objectKey)
                    .expiry(properties.presignedUrlExpirySeconds())
                    .build());

            return new PresignedUrl(url, Instant.now().plusSeconds(properties.presignedUrlExpirySeconds()));
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to create MinIO object link.", exception);
        }
    }

    @Override
    public InputStream getObject(BucketType bucketType, String objectKey) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName(bucketType))
                    .object(objectKey)
                    .build());
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to get MinIO object.", exception);
        }
    }

    @Override
    public StoredObject putObject(BucketType bucketType, MultipartFile file) {
        String objectKey = createObjectKey(file.getOriginalFilename());

        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName(bucketType))
                    .object(objectKey)
                    .stream(file.getInputStream(), file.getSize(), -1L)
                    .contentType(file.getContentType())
                    .build());

            return new StoredObject(objectKey, file.getContentType(), file.getSize());
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to upload object to MinIO.", exception);
        }
    }

    @Override
    public void deleteObject(BucketType bucketType, String objectKey) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName(bucketType))
                    .object(objectKey)
                    .build());
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to delete MinIO object.", exception);
        }
    }

    private void ensureBucketExists(String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());

            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to initialize MinIO bucket: " + bucketName, exception);
        }
    }

    private String bucketName(BucketType bucketType) {
        return switch (bucketType) {
            case FILE -> properties.fileBucket();
            case THUMBNAIL -> properties.thumbnailBucket();
        };
    }

    private String createObjectKey(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return UUID.randomUUID().toString();
        }

        return UUID.randomUUID() + "-" + originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String createObjectKey() {
        return UUID.randomUUID().toString();
    }

    @Override
    public StoredObject putObject(BucketType bucketType, byte[] file) {
        String objectKey = createObjectKey();

        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName(bucketType))
                    .object(objectKey)
                    .stream(new ByteArrayInputStream(file), (long) file.length, -1L)
                    .contentType("image/jpeg")
                    .build());

            return new StoredObject(objectKey, "image/jpeg", (long) file.length);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to upload object to MinIO.", exception);
        }
    }
}
