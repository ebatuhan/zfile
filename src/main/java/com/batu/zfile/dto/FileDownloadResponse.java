package com.batu.zfile.dto;

public record FileDownloadResponse(String filename, Long size, String contentType, String url) {
}
