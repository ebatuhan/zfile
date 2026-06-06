package com.batu.zfile.node.dto;

import java.time.Instant;

public record NodeDownloadDTO(String presignedUrl, Instant expiresAt) {

}
