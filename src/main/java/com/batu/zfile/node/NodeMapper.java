package com.batu.zfile.node;

import org.springframework.stereotype.Component;

import com.batu.zfile.node.dto.NodeItemDTO;

@Component
public class NodeMapper {

    public NodeItemDTO toDto(Node node) {
        return new NodeItemDTO(node.getNodeId(),
                node.getName(),
                node.getType().toString(),
                node.getCreatedAt(),
                node.getUpdatedAt());
    }
}
