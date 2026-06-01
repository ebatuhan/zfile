package com.batu.zfile.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.batu.zfile.repository.PendingObjectDeletionRepository;
import com.batu.zfile.service.ObjectStorageService;

@Component
public class PendingObjectDeletionJob {

    private final PendingObjectDeletionRepository pendingObjectDeletionRepository;
    private final ObjectStorageService objectStorageService;

    public PendingObjectDeletionJob(
            PendingObjectDeletionRepository pendingObjectDeletionRepository,
            ObjectStorageService objectStorageService) {
        this.pendingObjectDeletionRepository = pendingObjectDeletionRepository;
        this.objectStorageService = objectStorageService;
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

        objectStorageService.deleteAll(objectKeys);
        pendingObjectDeletionRepository.deleteAll(pendingDeletions);
    }
}
