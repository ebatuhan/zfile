package com.batu.zfile.metadata;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, UUID>{

}
