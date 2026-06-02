package com.batu.zfile.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.batu.zfile.entity.Thumbnail;
import com.batu.zfile.entity.ThumbnailStatus;

public interface ThumbnailRepository extends JpaRepository<Thumbnail, UUID> {

    @EntityGraph(attributePaths = "metadata")
    List<Thumbnail> findByMetadataFileMetadataIdInAndStatus(Collection<UUID> fileMetadataIds, ThumbnailStatus status);

    @EntityGraph(attributePaths = "metadata")
    Optional<Thumbnail> findByMetadataFileMetadataIdAndStatus(UUID fileMetadataId, ThumbnailStatus status);
}
