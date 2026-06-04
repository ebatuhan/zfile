package com.batu.zfile.node.dto;

import java.time.Instant;
import java.util.UUID;

public record NodeDTO(
    UUID nodeId,
    String nodeName,
    String nodeType,
    Instant createdAt,
    Instant updatedAt
) {

}
