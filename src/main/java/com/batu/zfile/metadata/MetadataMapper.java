package com.batu.zfile.metadata;

import org.springframework.stereotype.Component;

import com.batu.zfile.metadata.dto.FileMetadataResponseDTO;

@Component
public class MetadataMapper {
    public FileMetadataResponseDTO toDto(FileMetadata metadata){
        return new FileMetadataResponseDTO(metadata.getFileMetadataId(), metadata.getContentType());
    }
}
