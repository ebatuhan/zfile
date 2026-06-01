package com.batu.zfile.dto.converter;

import org.springframework.stereotype.Component;

import com.batu.zfile.dto.FileNodeResponse;
import com.batu.zfile.dto.PathSegmentResponse;
import com.batu.zfile.entity.FileNode;
import com.batu.zfile.repository.FileNodeRepository;

@Component
public class FileNodeConverter {

    private final FileMetadataConverter fileMetadataConverter;

    public FileNodeConverter(FileMetadataConverter fileMetadataConverter) {
        this.fileMetadataConverter = fileMetadataConverter;
    }

    public FileNodeResponse toResponse(FileNode node) {
        return new FileNodeResponse(
                node.getFileNodeId(),
                node.getParent() == null ? null : node.getParent().getFileNodeId(),
                node.getType(),
                node.getName(),
                fileMetadataConverter.toResponse(node.getMetadata()),
                node.getCreatedAt(),
                node.getUpdatedAt());
    }

    public PathSegmentResponse toPathSegmentResponse(FileNodeRepository.PathSegmentProjection projection) {
        return new PathSegmentResponse(projection.getId(), projection.getName());
    }
}
