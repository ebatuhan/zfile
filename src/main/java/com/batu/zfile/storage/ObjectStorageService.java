package com.batu.zfile.storage;

import org.springframework.web.multipart.MultipartFile;

public interface ObjectStorageService {
    String getPresignedUrlByObjectKey(String objectKey);
    StoredObject storeFile(MultipartFile file);
}
