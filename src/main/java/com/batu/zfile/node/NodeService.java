package com.batu.zfile.node;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

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
    Node save(Node node);
    Node read(UUID nodeId);
    NodeDTO updateNodeById(UUID nodeId, NodeRequestDTO request);
    NodeDownloadDTO downloadNodeFile(UUID nodeId);
    NodeDTO createFileNode(UUID parentId, MultipartFile file);
    NodeDTO createFolderNode(NodeRequestDTO request);
}
