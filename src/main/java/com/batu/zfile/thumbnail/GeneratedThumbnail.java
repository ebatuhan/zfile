package com.batu.zfile.thumbnail;

import java.io.InputStream;

public record GeneratedThumbnail(
        InputStream content,
        long size,
        String contentType) {
}
