package com.batu.zfile.cleanup;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.batu.zfile.cleanup.PendingObjectDeletion;

public interface PendingObjectDeletionRepository extends JpaRepository<PendingObjectDeletion, UUID> {

    List<PendingObjectDeletion> findTop100ByOrderByCreatedAtAsc();
}