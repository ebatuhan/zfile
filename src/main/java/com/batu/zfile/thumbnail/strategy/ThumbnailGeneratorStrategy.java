package com.batu.zfile.thumbnail.strategy;

import java.io.InputStream;

public interface ThumbnailGeneratorStrategy {
    byte[] generateThumbnail(InputStream source);
}
