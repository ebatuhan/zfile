package com.batu.zfile.dto;

import java.time.Instant;
import java.util.UUID;

import com.batu.zfile.entity.NodeType;

public record FileNodeResponse(
        UUID id,
        UUID parentId,
        NodeType type,
        String name,
        FileMetadataResponse metadata,
        String thumbnailUrl,
        Instant createdAt,
        Instant updatedAt) {
}
