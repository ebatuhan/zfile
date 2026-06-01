package com.batu.zfile.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.batu.zfile.entity.FileMetadata;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, UUID> {

    Optional<FileMetadata> findByNode_FileNodeId(UUID fileNodeId);
}
