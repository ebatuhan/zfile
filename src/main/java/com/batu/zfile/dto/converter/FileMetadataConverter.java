package com.batu.zfile.dto.converter;

import org.springframework.stereotype.Component;

import com.batu.zfile.dto.FileMetadataResponse;
import com.batu.zfile.entity.FileMetadata;

@Component
public class FileMetadataConverter {

    public FileMetadataResponse toResponse(FileMetadata metadata) {
        if (metadata == null) {
            return null;
        }

        return new FileMetadataResponse(metadata.getObjectKey(), metadata.getSize(), metadata.getContentType());
    }
}
