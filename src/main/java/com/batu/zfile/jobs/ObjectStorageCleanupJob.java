package com.batu.zfile.jobs;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.batu.zfile.cleanup.PendingObjectDeletionService;

@Component
public class ObjectStorageCleanupJob {
    private final PendingObjectDeletionService pendingObjectDeletionService;

    public ObjectStorageCleanupJob(PendingObjectDeletionService pendingObjectDeletionService) {
        this.pendingObjectDeletionService = pendingObjectDeletionService;
    }

    @Scheduled(fixedDelay = 60_000 * 60)
    void doCleanupJob() {
        pendingObjectDeletionService.deletePendingObjects();
    }
}
