package com.batu.zfile.storage;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import com.batu.zfile.storage.enums.BucketType;

public interface ObjectStorageClient {
    void ensureBucketsExist();
    PresignedUrl getObjectLink(BucketType bucketType, String objectKey);
    InputStream getObject(BucketType bucketType, String objectKey);
    StoredObject putObject(BucketType bucketType, MultipartFile file);
    StoredObject putObject(BucketType bucketType, byte[] file);
    void deleteObject(BucketType bucketType, String objectKey);
}
