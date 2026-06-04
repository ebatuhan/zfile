package com.batu.zfile.node;

import org.springframework.stereotype.Component;

import com.batu.zfile.metadata.MetadataMapper;
import com.batu.zfile.node.dto.NodeDTO;
import com.batu.zfile.node.dto.NodeDetailsDTO;
import com.batu.zfile.node.dto.NodePathDTO;

@Component
public class NodeMapper {

    private final MetadataMapper metadataMapper;

    public NodeMapper(MetadataMapper metadataMapper) {
        this.metadataMapper = metadataMapper;
    }

    public NodeDTO toNodeItemDto(Node node) {
        return new NodeDTO(node.getNodeId(),
                node.getName(),
                node.getType().toString(),
                node.getCreatedAt(),
                node.getUpdatedAt());
    }

    public NodePathDTO toNodePathDTO(PathSegmentProjection pathSegment) {
        return new NodePathDTO(pathSegment.getNodeId(), pathSegment.getName());
    }

    public NodeDetailsDTO toNodeDetailsDTO(Node node) {
        return new NodeDetailsDTO(node.getNodeId(),
                node.getName(),
                node.getType().toString(),
                metadataMapper.toDto(node.getMetadata()),
                node.getCreatedAt(),
                node.getUpdatedAt());
    }
}
