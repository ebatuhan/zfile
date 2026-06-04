package com.batu.zfile.node;

import java.util.List;
import java.util.UUID;

import com.batu.zfile.common.CursorPageResponse;
import com.batu.zfile.node.dto.NodeDTO;
import com.batu.zfile.node.dto.NodeDetailsDTO;
import com.batu.zfile.node.dto.NodeDownloadDTO;
import com.batu.zfile.node.dto.NodePathDTO;
import com.batu.zfile.node.dto.NodeRequestDTO;


public interface NodeService {
    CursorPageResponse<NodeDTO> getNodes(UUID parentId, String cursorPosition);
    NodeDTO getNodeById(UUID nodeId);
    List<NodePathDTO> getPathByNodeId(UUID nodeId);
    NodeDetailsDTO getNodeDetails(UUID nodeId);
    NodeDTO updateNodeById(UUID nodeId, NodeRequestDTO request);
    NodeDownloadDTO downloadNodeFile(UUID nodeId);
}
