package com.batu.zfile.storage;


public record StoredObject(
    String objectKey,
    String mimeType,
    Long sizeInBytes
) {
    
}
