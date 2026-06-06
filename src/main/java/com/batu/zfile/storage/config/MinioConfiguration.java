package com.batu.zfile.storage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MinioConfiguration.MinioProperties.class)
public class MinioConfiguration {

    @Bean
    io.minio.MinioClient minioClient(MinioProperties properties) {
        return io.minio.MinioClient.builder()
                .endpoint(properties.endpoint())
                .credentials(properties.accessKey(), properties.secretKey())
                .region(properties.region())
                .build();
    }

    @ConfigurationProperties(prefix = "zfile.storage.minio")
    public record MinioProperties(
            String endpoint,
            String accessKey,
            String secretKey,
            String fileBucket,
            String thumbnailBucket,
            String region,
            int presignedUrlExpirySeconds) {
    }
}
