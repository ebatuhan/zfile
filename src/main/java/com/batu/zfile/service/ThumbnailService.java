package com.batu.zfile.service;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import com.batu.zfile.entity.FileMetadata;
import com.batu.zfile.entity.Thumbnail;

public interface ThumbnailService {

    Thumbnail createFor(FileMetadata metadata);

    Map<UUID, String> getThumbnailUrls(Collection<FileMetadata> metadata);

    void generateForMetadata(UUID fileMetadataId);

    void deleteAllByObjectKeys(Collection<String> objectKeys);
}
