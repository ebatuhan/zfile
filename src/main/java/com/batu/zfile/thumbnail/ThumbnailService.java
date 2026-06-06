package com.batu.zfile.thumbnail;

import java.util.UUID;

import com.batu.zfile.thumbnail.dto.ThumbnailDownloadDTO;

public interface ThumbnailService {
    ThumbnailDownloadDTO downloadThumbnail(UUID thumbnailId);
    void generateThumbnailForFileAndSave(UUID fileNodeId);
}
