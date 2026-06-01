package com.batu.zfile.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;

@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfiguration {

    @Bean
    public MinioClient minioClient(MinioProperties properties) {
        return MinioClient.builder()
                .endpoint(properties.endpoint())
                .credentials(properties.accessKey(), properties.secretKey())
                .region(properties.region())
                .build();
    }

    @Bean
    public MinioClient publicMinioClient(MinioProperties properties) {
        return MinioClient.builder()
                .endpoint(properties.publicEndpoint())
                .credentials(properties.accessKey(), properties.secretKey())
                .region(properties.region())
                .build();
    }

    @Bean
    public MinioBucketInitializer minioBucketInitializer(MinioClient minioClient, MinioProperties properties) {
        return new MinioBucketInitializer(minioClient, properties);
    }

    public record MinioBucketInitializer(MinioClient minioClient, MinioProperties properties) {

        public MinioBucketInitializer {
            try {
                var exists = minioClient.bucketExists(BucketExistsArgs.builder()
                        .bucket(properties.bucket())
                        .build());

                if (!exists) {
                    minioClient.makeBucket(MakeBucketArgs.builder()
                            .bucket(properties.bucket())
                            .region(properties.region())
                            .build());
                }
            } catch (Exception exception) {
                throw new IllegalStateException("Failed to initialize MinIO bucket", exception);
            }
        }
    }
}
