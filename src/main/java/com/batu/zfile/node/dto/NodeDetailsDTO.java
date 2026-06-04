package com.batu.zfile.node.dto;

import java.time.Instant;
import java.util.UUID;

import com.batu.zfile.metadata.dto.FileMetadataDTO;

public record NodeDetailsDTO(
    UUID nodeId,
    String nodeName,
    String nodeType,
    FileMetadataDTO metadata,
    Instant createdAt,
    Instant updatedAt
) {

}
