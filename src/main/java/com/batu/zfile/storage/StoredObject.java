package com.batu.zfile.storage;


public record StoredObject(
    String objectKey,
    String mimeType,
    long sizeInBytes
) {
    
}
