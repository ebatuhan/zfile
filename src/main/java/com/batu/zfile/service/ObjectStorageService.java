package com.batu.zfile.service;

import java.io.InputStream;
import java.util.Collection;

import org.springframework.web.multipart.MultipartFile;

import com.batu.zfile.entity.FileMetadata;

public interface ObjectStorageService {

    FileMetadata upload(MultipartFile file);

    InputStream read(ObjectBucket bucket, String objectKey);

    void upload(ObjectBucket bucket, String objectKey, InputStream content, long size, String contentType);

    String createPresignedUrl(ObjectBucket bucket, String objectKey);

    void deleteAll(ObjectBucket bucket, Collection<String> objectKeys);
}
