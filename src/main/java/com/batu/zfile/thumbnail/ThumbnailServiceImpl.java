package com.batu.zfile.thumbnail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import com.batu.zfile.common.ResourceNotFoundException;
import com.batu.zfile.node.Node;
import com.batu.zfile.node.NodeService;
import com.batu.zfile.storage.ObjectStorageService;
import com.batu.zfile.storage.PresignedUrl;
import com.batu.zfile.storage.StoredObject;
import com.batu.zfile.thumbnail.dto.ThumbnailDownloadDTO;
import com.batu.zfile.thumbnail.factory.ThumbnailGeneratorFactory;
import com.batu.zfile.thumbnail.strategy.ThumbnailGeneratorStrategy;

public class ThumbnailServiceImpl implements ThumbnailService {

    private final ObjectStorageService objectStorageService;
    private final ThumbnailRepository thumbnailRepository;
    private final NodeService nodeService;
    private final ThumbnailGeneratorFactory generatorFactory;

    public ThumbnailServiceImpl(ObjectStorageService objectStorageService, ThumbnailRepository thumbnailRepository,
            NodeService nodeService, ThumbnailGeneratorFactory generatorFactory) {
        this.objectStorageService = objectStorageService;
        this.thumbnailRepository = thumbnailRepository;
        this.nodeService = nodeService;
        this.generatorFactory = generatorFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public ThumbnailDownloadDTO downloadThumbnail(UUID thumbnailId) {

        Thumbnail thumbnail = thumbnailRepository.findById(thumbnailId)
                .orElseThrow(() -> new ResourceNotFoundException("Thumbnail not exists."));

        PresignedUrl presignedUrl = objectStorageService.getThumbnailLink(thumbnail.getObjectKey());

        return new ThumbnailDownloadDTO(presignedUrl.url(), presignedUrl.expiresAt());
    }


    //Pass object key and content type later, in a postprocessor serice.
    @Override
    @Async
    @Transactional 
    public void generateThumbnailForFileAndSave(UUID fileNodeId) {

        Node node = nodeService.read(fileNodeId);

        final String objectKey = node.getMetadata().getObjectKey();
        final String contentType = node.getMetadata().getContentType();

        Optional<ThumbnailGeneratorStrategy> generator = generatorFactory.getStrategy(contentType);

        if (!generator.isPresent()) {
            return;
        }

        byte[] generatedThumbnail;

        try (InputStream source = objectStorageService.getFileStream(objectKey)) {
            generatedThumbnail = generator.get().generateThumbnail(source);
        } catch (IOException e) {
            throw new IllegalStateException("Thumbnail generation failed.");
        }

        StoredObject storedThumbnail = objectStorageService.storeThumbnail(generatedThumbnail);

        Thumbnail thumbnail = Thumbnail.builder()
                .metadata(node.getMetadata())
                .objectKey(storedThumbnail.objectKey())
                .build();

        thumbnailRepository.save(thumbnail);
    }

}
