package com.batu.zfile.thumbnail;

import java.io.InputStream;

public interface ThumbnailGenerator {

    boolean supports(String contentType);

    GeneratedThumbnail generate(InputStream source, String contentType);
}
