package com.batu.zfile.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.batu.zfile.repository.PendingObjectDeletionRepository;
import com.batu.zfile.service.ObjectBucket;
import com.batu.zfile.service.ObjectStorageService;
import com.batu.zfile.service.ThumbnailService;

@Component
public class PendingObjectDeletionJob {

    private final PendingObjectDeletionRepository pendingObjectDeletionRepository;
    private final ObjectStorageService objectStorageService;
    private final ThumbnailService thumbnailService;

    public PendingObjectDeletionJob(
            PendingObjectDeletionRepository pendingObjectDeletionRepository,
            ObjectStorageService objectStorageService,
            ThumbnailService thumbnailService) {
        this.pendingObjectDeletionRepository = pendingObjectDeletionRepository;
        this.objectStorageService = objectStorageService;
        this.thumbnailService = thumbnailService;
    }

    @Scheduled(fixedDelayString = "${zfile.storage.cleanup-delay-ms:60000}")
    public void cleanupPendingObjectDeletions() {
        var pendingDeletions = pendingObjectDeletionRepository.findTop100ByOrderByCreatedAtAsc();
        if (pendingDeletions.isEmpty()) {
            return;
        }

        var objectKeys = pendingDeletions.stream()
                .map(pendingDeletion -> pendingDeletion.getObjectKey())
                .toList();

        thumbnailService.deleteAllByObjectKeys(objectKeys);
        objectStorageService.deleteAll(ObjectBucket.FILE, objectKeys);
        pendingObjectDeletionRepository.deleteAll(pendingDeletions);
    }
}
