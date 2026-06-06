package com.batu.zfile.storage;

import java.io.InputStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.batu.zfile.storage.enums.BucketType;

@Service
public class DefaultObjectStorageService implements ObjectStorageService {

    private final ObjectStorageClient objectStorageClient;

    public DefaultObjectStorageService(ObjectStorageClient objectStorageClient) {
        this.objectStorageClient = objectStorageClient;
    }

    @Override
    public PresignedUrl getFileLink(String objectKey) {
        return objectStorageClient.getObjectLink(BucketType.FILE, objectKey);
    }

    @Override
    public PresignedUrl getThumbnailLink(String objectKey) {
        return objectStorageClient.getObjectLink(BucketType.THUMBNAIL, objectKey);
    }

    @Override
    public StoredObject storeFile(MultipartFile file) {
        return objectStorageClient.putObject(BucketType.FILE, file);
    }

    @Override
    public StoredObject storeThumbnail(byte[] thumbnail) {
        return objectStorageClient.putObject(BucketType.THUMBNAIL, thumbnail);
    }

    @Override
    public InputStream getFileStream(String objectKey) {
        return objectStorageClient.getObject(BucketType.FILE, objectKey);
    }
}
