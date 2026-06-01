package com.batu.zfile.service;

import java.util.UUID;

import com.batu.zfile.entity.FileMetadata;
import com.batu.zfile.entity.FileNode;

public interface FileMetadataService {

    FileMetadata createMetadata(FileNode node, String objectKey, Long size, String contentType);

    FileMetadata getMetadataByNodeId(UUID nodeId);
}
