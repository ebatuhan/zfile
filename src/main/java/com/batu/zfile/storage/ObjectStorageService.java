package com.batu.zfile.storage;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public interface ObjectStorageService {
    PresignedUrl getFileLink(String objectKey);
    PresignedUrl getThumbnailLink(String objectKey);
    InputStream getFileStream(String objectKey);
    StoredObject storeFile(MultipartFile file);
    StoredObject storeThumbnail(byte[] thumbnail);
}
