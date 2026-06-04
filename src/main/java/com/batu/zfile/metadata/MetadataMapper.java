package com.batu.zfile.metadata;

import org.springframework.stereotype.Component;

import com.batu.zfile.metadata.dto.FileMetadataDTO;

@Component
public class MetadataMapper {
    public FileMetadataDTO toDto(FileMetadata metadata) {
        return new FileMetadataDTO(metadata.getFileMetadataId(), metadata.getSize(), metadata.getContentType());
    }
}
