package com.batu.zfile.service;

import java.util.Collection;

import org.springframework.web.multipart.MultipartFile;

import com.batu.zfile.entity.FileMetadata;

public interface ObjectStorageService {

    FileMetadata upload(MultipartFile file);

    String createDownloadUrl(String objectKey);

    void delete(String objectKey);

    void deleteAll(Collection<String> objectKeys);
}
