package com.batu.zfile.thumbnail.dto;

import java.time.Instant;

public record ThumbnailDownloadDTO(String presignedUrl, Instant expiresAt) {

}

