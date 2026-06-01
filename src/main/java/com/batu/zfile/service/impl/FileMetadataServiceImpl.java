package com.batu.zfile.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.batu.zfile.entity.FileMetadata;
import com.batu.zfile.entity.FileNode;
import com.batu.zfile.repository.FileMetadataRepository;
import com.batu.zfile.service.FileMetadataService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class FileMetadataServiceImpl implements FileMetadataService {

    private final FileMetadataRepository fileMetadataRepository;

    public FileMetadataServiceImpl(FileMetadataRepository fileMetadataRepository) {
        this.fileMetadataRepository = fileMetadataRepository;
    }

    @Override
    @Transactional
    public FileMetadata createMetadata(FileNode node, String objectKey, Long size, String contentType) {
        var metadata = FileMetadata.builder()
                .node(node)
                .objectKey(objectKey)
                .size(size)
                .contentType(contentType)
                .build();

        return fileMetadataRepository.save(metadata);
    }

    @Override
    @Transactional(readOnly = true)
    public FileMetadata getMetadataByNodeId(UUID nodeId) {
        return fileMetadataRepository.findByNode_FileNodeId(nodeId)
                .orElseThrow(() -> new EntityNotFoundException("File metadata not found for node: " + nodeId));
    }
}
