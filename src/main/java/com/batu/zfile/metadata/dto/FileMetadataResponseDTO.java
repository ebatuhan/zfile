package com.batu.zfile.metadata.dto;

import java.util.UUID;

public record FileMetadataResponseDTO(
    UUID fileMetadataId,
    String contentType
) {

}
