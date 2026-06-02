package com.batu.zfile.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.batu.zfile.entity.FileMetadata;
import com.batu.zfile.entity.Thumbnail;
import com.batu.zfile.entity.ThumbnailStatus;
import com.batu.zfile.exception.ThumbnailGenerationException;
import com.batu.zfile.repository.ThumbnailRepository;
import com.batu.zfile.service.ObjectBucket;
import com.batu.zfile.service.ObjectStorageService;
import com.batu.zfile.service.ThumbnailService;
import com.batu.zfile.thumbnail.ThumbnailGenerator;

@Service
public class ThumbnailServiceImpl implements ThumbnailService {

    private final ObjectStorageService objectStorageService;
    private final ThumbnailRepository thumbnailRepository;
    private final List<ThumbnailGenerator> thumbnailGenerators;

    public ThumbnailServiceImpl(
            ObjectStorageService objectStorageService,
            ThumbnailRepository thumbnailRepository,
            List<ThumbnailGenerator> thumbnailGenerators) {
        this.objectStorageService = objectStorageService;
        this.thumbnailRepository = thumbnailRepository;
        this.thumbnailGenerators = thumbnailGenerators;
    }

    @Override
    public Thumbnail createFor(FileMetadata metadata) {
        var contentType = metadata == null ? null : metadata.getContentType();
        var supported = thumbnailGenerators.stream()
                .anyMatch(generator -> generator.supports(contentType));

        return Thumbnail.builder()
                .metadata(metadata)
                .status(supported ? ThumbnailStatus.PENDING : ThumbnailStatus.UNSUPPORTED)
                .build();
    }

    @Override
    public Map<UUID, String> getThumbnailUrls(Collection<FileMetadata> metadata) {
        if (metadata.isEmpty()) {
            return Map.of();
        }

        var metadataIds = metadata.stream()
                .map(FileMetadata::getFileMetadataId)
                .toList();

        if (metadataIds.isEmpty()) {
            return Map.of();
        }

        return thumbnailRepository.findByMetadataFileMetadataIdInAndStatus(metadataIds, ThumbnailStatus.READY)
                .stream()
                .collect(Collectors.toMap(
                        thumbnail -> thumbnail.getMetadata().getFileMetadataId(),
                        thumbnail -> objectStorageService.createPresignedUrl(
                                ObjectBucket.THUMBNAIL,
                                thumbnail.getMetadata().getObjectKey())));
    }

    @Override
    @Transactional
    public void generateForMetadata(UUID fileMetadataId) {
        var thumbnail = thumbnailRepository.findByMetadataFileMetadataIdAndStatus(fileMetadataId, ThumbnailStatus.PENDING)
                .orElseThrow(() -> new IllegalStateException("Pending thumbnail not found for metadata: " + fileMetadataId));

        var metadata = thumbnail.getMetadata();

        var objectKey = metadata.getObjectKey();
        var contentType = metadata.getContentType();
        var generator = thumbnailGenerators.stream()
                .filter(candidate -> candidate.supports(contentType))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Thumbnail generator not found for content type: " + contentType));

        try (var source = objectStorageService.read(ObjectBucket.FILE, objectKey)) {
            var generated = generator.generate(source, contentType);
            try (var content = generated.content()) {
                objectStorageService.upload(
                        ObjectBucket.THUMBNAIL,
                        objectKey,
                        content,
                        generated.size(),
                        generated.contentType());
            }
            thumbnail.setStatus(ThumbnailStatus.READY);
        } catch (Exception exception) {
            throw new ThumbnailGenerationException("Failed to generate thumbnail", exception);
        }
    }

    @Override
    public void deleteAllByObjectKeys(Collection<String> objectKeys) {
        objectStorageService.deleteAll(ObjectBucket.THUMBNAIL, objectKeys);
    }
}
