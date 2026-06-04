package com.batu.zfile.metadata.dto;

import java.util.UUID;

public record FileMetadataDTO(
    UUID fileMetadataId,
    Long size,
    String contentType
) {

}
