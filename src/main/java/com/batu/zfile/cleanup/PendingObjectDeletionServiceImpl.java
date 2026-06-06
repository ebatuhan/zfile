package com.batu.zfile.cleanup;

import java.util.List;

import org.springframework.stereotype.Service;

import com.batu.zfile.storage.ObjectStorageClient;

@Service
public class PendingObjectDeletionServiceImpl implements PendingObjectDeletionService {

    private final PendingObjectDeletionRepository pendingObjectDeletionRepository;
    private final ObjectStorageClient objectStorageClient;

    public PendingObjectDeletionServiceImpl(PendingObjectDeletionRepository pendingObjectDeletionRepository,
            ObjectStorageClient objectStorageClient) {
        this.pendingObjectDeletionRepository = pendingObjectDeletionRepository;
        this.objectStorageClient = objectStorageClient;
    }

    @Override
    public void deletePendingObjects() {
        List<PendingObjectDeletion> pendingObjectDeletions = pendingObjectDeletionRepository
                .findTop100ByOrderByCreatedAtAsc();

        pendingObjectDeletions.forEach(p -> objectStorageClient.deleteObject(p.getBucketType(), p.getObjectKey()));
    }

}
